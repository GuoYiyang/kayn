package com.kayn.kafka

import com.alibaba.fastjson.JSON
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


object KafkaStreaming {

  // list保存对象数据
  val queryList: mutable.MutableList[QueryMeta] = mutable.MutableList[QueryMeta]()
  val catList: mutable.MutableList[CatMeta] = mutable.MutableList[CatMeta]()
  val cartList: mutable.MutableList[CartMeta] = mutable.MutableList[CartMeta]()
  val orderList: mutable.MutableList[OrderMeta] = mutable.MutableList[OrderMeta]()
  val payList: mutable.MutableList[PayMeta] = mutable.MutableList[PayMeta]()

  def main(args: Array[String]): Unit = {

    val EsConfig = Map(
      "es.mapping.id" -> "username",
      "es.write.operation" -> "upsert"
    )

    val conf = new SparkConf().setAppName("SparkStreaming")
    conf.set("es.nodes", "localhost")
    conf.set("es.port", "9200")
    conf.set("es.index.auto.create", "true")
    conf.set("es.index.read.missing.as.empty", "true")



    val sc = new SparkContext(conf)
    val ssc = new StreamingContext(sc, Seconds(30))

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
        rdd.map(x => {
          val createTime = x.timestamp()
          val value = JSON.parseObject(x.value())
          val message = value.getString("message")
          val method = JSON.parseObject(value.getString("source")).getString("method")
          dataTransform(message, method, createTime)
          (message, method, createTime)
        }).foreach(x => {
          println(x)
        })
      }

      if (queryList.nonEmpty) {
        val queryRDD = sc.parallelize(queryList)
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

    })

    ssc.start()
    ssc.awaitTermination()
  }


  def dataTransform(message: String, method: String, createTime: Long): Unit = {
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
  }

}
