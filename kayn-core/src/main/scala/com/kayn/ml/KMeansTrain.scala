package com.kayn.ml

import java.util.Properties

import com.kayn.kafka.KafkaStreaming.{sparkConf, sparkContext}
import org.apache.spark.ml.clustering.{KMeans, KMeansModel}
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.{col, lit, row_number, udf, when}
import org.apache.spark.sql.types.LongType
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}
import org.elasticsearch.spark._


case class RFMMeta(
                    username: String,
                    recency: Long,
                    frequency: Long,
                    monetary: Float
                  )

object KMeansTrain {

  def Train(): Unit = {

    //创建spark session
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()
    import spark.implicits._

    val payRDD = sparkContext.esRDD("user_pay_order")

    val payDF = payRDD.map(x => {
      val recency = (System.currentTimeMillis() - x._2("lastTime").asInstanceOf[Long]) / (1000 * 60 * 60 * 24)
      val frequency = x._2("cnt").asInstanceOf[Long]
      val monetary = x._2("orderTotal").asInstanceOf[Float]
      RFMMeta(x._1, recency, frequency, monetary)
    }).toDF()

    // 按照规则，给RFM值打分: Score
      /*
      R: 1-3天=5分，4-6天=4分，7-9天=3分，10-15天=2分，大于16天=1分
      F: ≥200=5分，150-199=4分，100-149=3分，50-99=2分，1-49=1分
      M: ≥20w=5分，10-19w=4分，5-9w=3分，1-4w=2分，<1w=1分
      */
    // R 打分条件表达式
    val rWhen = when(col("recency").between(0, 3), 5.0) //
      .when(col("recency").between(4, 6), 4.0) //
      .when(col("recency").between(7, 9), 3.0) //
      .when(col("recency").between(10, 15), 2.0) //
      .when(col("recency").geq(16), 1.0) //
    // F 打分条件表达式
    val fWhen = when(col("frequency").between(1, 49), 1.0) //
      .when(col("frequency").between(50, 99), 2.0) //
      .when(col("frequency").between(100, 149), 3.0) //
      .when(col("frequency").between(150, 199), 4.0) //
      .when(col("frequency").geq(200), 5.0) //
    // M 打分条件表达式
    val mWhen = when(col("monetary").lt(1000), 1.0) //
      .when(col("monetary").between(1000, 4999), 2.0) //
      .when(col("monetary").between(5000, 9999), 3.0) //
      .when(col("monetary").between(10000, 19999), 4.0) //
      .when(col("monetary").geq(20000), 5.0) //

    val rfmScoreDF: DataFrame = payDF.select(
      $"username",
      rWhen.as("r_score"),
      fWhen.as("f_score"),
      mWhen.as("m_score")
    )

    // k-means 聚类算法
    // 组合R\F\M列为特征值features
    val assembler: VectorAssembler = new VectorAssembler()
      .setInputCols(Array("r_score", "f_score", "m_score"))
      .setOutputCol("features")
    val rfmFeaturesDF: DataFrame = assembler.transform(rfmScoreDF)
//    // 将训练数据缓存
//    rfmFeaturesDF.persist(StorageLevel.MEMORY_AND_DISK)
    // 使用KMeans聚类算法模型训练
    val kMeansModel: KMeansModel = new KMeans()
      .setFeaturesCol("features")
      .setPredictionCol("prediction")
      .setK(5)
      .setMaxIter(10)
      .fit(rfmFeaturesDF)
    // 模型评估
//    println(s"WSSSE = ${kMeansModel.computeCost(rfmFeaturesDF)}")
    // 使用模型预测
    val predictionDF: DataFrame = kMeansModel.transform(rfmFeaturesDF)

//    predictionDF.printSchema()
//    predictionDF.show(10, truncate = false)

    // 获取聚类模型中簇中心及索引
    import org.apache.spark.ml.linalg
    val clusterCenters: Array[linalg.Vector] = kMeansModel.clusterCenters
    val centerIndexArray: Array[((Int, Double), Int)] = clusterCenters
      .zipWithIndex
      .map{
        case(vector, centerIndex) => (centerIndex, vector.toArray.sum)
      }
      .sortBy{
        case(_, rfm) => - rfm
      }
      .zipWithIndex
//    centerIndexArray.foreach(println)

    // 聚类类簇关联属性标签数据rule，对应聚类类簇与标签tagId

    val rulesMap: Map[String, String] = Map(
      "0" -> "高价值",
      "1" -> "中高价值",
      "2" -> "中价值",
      "3" -> "中低价值",
      "4" -> "低价值"
    )
    val indexTagMap: Map[Int, String] = centerIndexArray
      .map{ case((centerIndex, _), index) =>
        val tagName  = rulesMap(index.toString)
        (centerIndex, tagName)
      }
      .toMap

    val indexTagMapBroadcast = spark.sparkContext.broadcast(indexTagMap)
    val index_to_tag = udf(
      (prediction: Int) => indexTagMapBroadcast.value(prediction)
    )
    val modelDF: DataFrame = predictionDF
      .select(

        $"username",
        index_to_tag($"prediction").as("rfm")
      )

    val sqlDF = modelDF.withColumn("id",row_number.over(Window.partitionBy(lit(1)).orderBy(lit(1))).cast(LongType))

    sqlDF.show(10)
    // 写入Mysql
    val mysqlProp = new Properties()
    mysqlProp.put("user", "root")
    mysqlProp.put("password", "123456")
    val url = "jdbc:mysql://localhost:3306/kayn?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai"
    sqlDF.write.mode(SaveMode.Overwrite).jdbc(url, "kayn_user_rmf", mysqlProp)

  }

}
