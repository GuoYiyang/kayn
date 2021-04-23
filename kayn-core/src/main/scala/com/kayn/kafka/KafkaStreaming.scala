package com.kayn.kafka

import com.alibaba.fastjson.JSON
import com.kayn.ml.KMeansTrain.Train
import org.apache.kafka.common.serialization.StringDeserializer
import org.elasticsearch.spark._
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}

import scala.collection.mutable


case class QueryMeta(
                      username: String,
                      query: String,
                      time: Long
                    )
case class QueryCnt(
                     query: String,
                     cnt: Int
                   )
case class QueryCntList(
                      username: String,
                      query: mutable.MutableList[QueryCnt]
                    )



case class CatMeta(
                    username: String,
                    productName: String,
                    cat: String,
                    time: Long
                  )
case class CatCnt(
                  cat: String,
                  cnt: Int
                 )
case class CatCntList(
                     username: String,
                     cat: mutable.MutableList[CatCnt]
                     )


case class CartMeta(
                     username: String,
                     productName: String,
                     salePrice: Double,
                     productNum: Int,
                     time: Long
                   )

case class OrderMeta(
                      username: String,
                      orderTotal: Double,
                      tel: String,
                      street: String,
                      time: Long
                    )

case class PayMeta(
                    username: String,
                    orderTotal: Double,
                    time: Long
                  )
case class PayCnt(
                 username: String,
                 orderTotal: Double,
                 cnt: Int,
                 lastTime: Long
                 )


object KafkaStreaming {

  // list保存对象数据
  val queryList: mutable.MutableList[QueryMeta] = mutable.MutableList[QueryMeta]()
  val catList: mutable.MutableList[CatMeta] = mutable.MutableList[CatMeta]()
  val cartList: mutable.MutableList[CartMeta] = mutable.MutableList[CartMeta]()
  val orderList: mutable.MutableList[OrderMeta] = mutable.MutableList[OrderMeta]()
  val payList: mutable.MutableList[PayMeta] = mutable.MutableList[PayMeta]()

  val sparkConf: SparkConf = new SparkConf().setAppName("SparkStreaming").setMaster("local[*]")
  sparkConf.set("es.nodes", "localhost")
  sparkConf.set("es.port", "9200")
  sparkConf.set("es.index.auto.create", "true")
  sparkConf.set("es.index.read.missing.as.empty", "true")

  val sparkContext = new SparkContext(sparkConf)

  def main(args: Array[String]): Unit = {

    val EsConfig = Map(
      "es.mapping.id" -> "username",
      "es.write.operation" -> "upsert"
    )
    val ssc = new StreamingContext(sparkContext, Seconds(30))

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "kafka_log_topic",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )
    val topic = Set("kafka_log_topic")

    val data = KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](topic, kafkaParams)
    )

    data.foreachRDD(rdd => {
      if (!rdd.isEmpty()) {
        //数据业务逻辑处理
        rdd
          .filter(x => {
            JSON.parseObject(x.value()).getString("message") != null
          })
          .map(x => {
          val createTime = x.timestamp()
          val value = JSON.parseObject(x.value())
          val message = value.getString("message")
          val method = JSON.parseObject(value.getString("source")).getString("method")
          dataTransform(message, method, createTime)
          (message, method, createTime)
        })
          .foreach(x => {
          println(x)
        })
      }

      if (queryList.nonEmpty) {
        val queryRDD = sparkContext.parallelize(queryList)
        queryRDD
          .map(x => {
            val key = x.username + "_" + x.query
            (key, 1)
          })
          .reduceByKey(_ + _)
          .map(x => {
            (x._1.split("_")(0),(x._1.split("_")(1), x._2))
          })
          .groupByKey()
          .map(x => {
            var list = mutable.MutableList[QueryCnt]()
            x._2.foreach(y => {
              list += QueryCnt(y._1, y._2)
            })
            QueryCntList(x._1, list)
          })
          .saveToEs("user_query_cnt", EsConfig)
      }

      if (catList.nonEmpty) {
        val catRDD = sparkContext.parallelize(catList)
        catRDD
          .map(x => {
            val key = x.username + "_" + x.cat
            (key, 1)
          })
          .reduceByKey(_ + _)
          .map(x => {
            (x._1.split("_")(0),(x._1.split("_")(1), x._2))
          })
          .groupByKey()
          .map(x => {
            var list = mutable.MutableList[CatCnt]()
            x._2.foreach(y => {
              list += CatCnt(y._1, y._2)
            })
            CatCntList(x._1, list)
          })
          .saveToEs("user_cat_cnt", EsConfig)
      }

      if (payList.nonEmpty) {
        val payRDD = sparkContext.parallelize(payList)
        payRDD
          .map(x => {
            (x.username, x.orderTotal)
          })
          .groupByKey()
          .map(x => {
            var orderTotal: Double = 0d
            var payCnt: Int = 0
            x._2.foreach(y => {
              orderTotal += y
              payCnt += 1
            })
            PayCnt(x._1, orderTotal, payCnt, System.currentTimeMillis())
          })
          .saveToEs("user_pay_order", EsConfig)
          // k-means 聚类分析
          Train()
      }
    })

    ssc.start()
    ssc.awaitTermination()
  }


  def dataTransform(message: String, method: String, createTime: Long): Unit = {
    try {
      val msg = JSON.parseObject(message)
      val un = msg.getString("username")

      if (method.equals("getGoodPage")) {
        val q = msg.getString("query")
        queryList += QueryMeta(username = un, query = q, time = createTime)
      } else if (method.equals("getGoodDetail")) {
        val cn = msg.getString("catName")
        val pn = msg.getString("productName")
        catList += CatMeta(username = un, productName = pn, cat = cn, time = createTime)
      } else if (method.equals("addCart")) {
        val pn = msg.getString("productName")
        val sp = msg.getDouble("salePrice")
        val num = msg.getInteger("productNum")
        cartList += CartMeta(username = un, productName = pn, salePrice = sp, productNum = num, time = createTime)
      } else if (method.equals("addOrder")) {
        val tel = msg.getString("tel")
        val st = msg.getString("street")
        val total = msg.getString("orderTotal").toDouble
        orderList += OrderMeta(username = un, orderTotal = total, tel = tel, street = st, time = createTime)
      } else if (method.equals("payOrder")) {
        val total = msg.getString("orderTotal").toDouble
        payList += PayMeta(username = un, orderTotal = total, time = createTime)
      }
    } catch {
      case e: Exception =>
        e.printStackTrace() // 打印到标准err
    }
  }

}
