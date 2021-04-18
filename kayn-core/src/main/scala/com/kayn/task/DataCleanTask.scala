package com.kayn.task

import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.{Executors, TimeUnit}

import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}
import org.elasticsearch.spark._

import scala.collection.mutable


case class AddCart(
                  username: String,
                  totalPrice: Float,
                  addCartCnt: Int,
                  timeStamp: Long
                  )

case class PayOrder(
                    username: String,
                    totalPrice: Float,
                    payOrderCnt: Int,
                    timeStamp: Long
                  )

case class Tel(
              tel: String,
              cnt: Int
              )

case class Cat(
                cat: String,
                cnt: Int
              )

case class Query(
                query: String,
                cnt: Int
                )


case class PreferTel(
                   username: String,
                   preferTel: mutable.MutableList[Tel],
                   timeStamp: Long
                   )

/**
 * 用户最喜爱的类别
 * @param username 用户名
 * @param preferCat 浏览类别次数列表
 * @param cnt 总浏览次数
 * @param timeStamp: Long
 */
case class PreferCat(
                      username: String,
                      preferCat: mutable.MutableList[Cat],
                      cnt : Int,
                      timeStamp: Long
                    )

case class LastMostQuery(
                        username: String,
                        query: mutable.MutableList[Query],
                        cnt : Int,
                        timeStamp: Long
                        )

object DataCleanTask {

  def main(args: Array[String]): Unit = {
    val runnable = new Runnable {
      override def run(): Unit = {
        dataClean()
      }
    }
    val service = Executors.newSingleThreadScheduledExecutor()
    // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
    service.scheduleAtFixedRate(runnable, 0, 60, TimeUnit.SECONDS)
  }

  def dataClean(): Unit = {

    val format = new SimpleDateFormat("yyyy.MM.dd")
    val currentDate = format.format(new Date())

    val config = Map(
      "es.nodes" -> "localhost",
      "es.port" -> "9200",
      "es.index.auto.create" -> "true",
      "es.index.read.missing.as.empty" -> "true"
    )

    val writeConfig = Map(
      "es.mapping.id" -> "username",
      "es.write.operation" -> "upsert"
    )

    //创建spark config
    val sparkConf = new SparkConf().setAppName("ES2Spark").setMaster("local[*]")
    sparkConf.set("es.nodes", config("es.nodes"))
    sparkConf.set("es.port", config("es.port"))
    sparkConf.set("es.index.auto.create", config("es.index.auto.create"))
    sparkConf.set("es.index.read.missing.as.empty", config("es.index.read.missing.as.empty"))

    val addCartQuery =
      """
        |{
        |  "query": {
        |    "match": {
        |      "source.method": "addCart"
        |    }
        |  }
        |}
        |""".stripMargin

    val addOrderQuery =
      """
        |{
        |  "query": {
        |    "match": {
        |      "source.method": "addOrder"
        |    }
        |  }
        |}
        |""".stripMargin

    val payOrderQuery =
      """
        |{
        |  "query": {
        |    "match": {
        |      "source.method": "payOrder"
        |    }
        |  }
        |}
        |""".stripMargin

    val getGoodDetailQuery =
      """
        |{
        |  "query": {
        |    "match": {
        |      "source.method": "getGoodDetail"
        |    }
        |  }
        |}
        |""".stripMargin

    val getGoodPageQuery =
      """
        |{
        |  "query": {
        |    "match": {
        |      "source.method": "getGoodPage"
        |    }
        |  }
        |}
        |""".stripMargin

    // 从es中读取数据
    val sparkContext = new SparkContext(sparkConf)

    val addCartEsRDD = sparkContext.esRDD("kafka-log-" + currentDate, addCartQuery)
    val addOrderEsRDD = sparkContext.esRDD("kafka-log-" + currentDate, addOrderQuery)
    val payOrderEsRDD = sparkContext.esRDD("kafka-log-" + currentDate, payOrderQuery)
    val getGoodDetailEsRDD = sparkContext.esRDD("kafka-log-" + currentDate, getGoodDetailQuery)
    val getGoodPageEsRDD = sparkContext.esRDD("kafka-log-" + currentDate, getGoodPageQuery)

    //创建spark session
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()

    // 统计用户今日添加购物车数据
    addCartEsRDD
      .map(x => {
        val doc = x._2("doc").asInstanceOf[mutable.Map[String, String]]
        (doc("username"), doc("productId"), doc("salePrice"))
      })
      .groupBy(x => {
        x._1
      })
      .map(x => {
        var totalPrice = 0f
        var addCartCnt = 0
        x._2.foreach(y => {
          totalPrice += y._3.asInstanceOf[Float]
          addCartCnt += 1
        })
        AddCart(x._1, totalPrice, addCartCnt, System.currentTimeMillis())
      })
      .saveToEs("add_cart", writeConfig)


    // 统计最常用的手机号
    addOrderEsRDD
      .map(x => {
        val doc = x._2("doc").asInstanceOf[mutable.Map[String, String]]
        (doc("username"), doc("tel"))
      })
      .map(x => {
        val key = x._1 + "_" + x._2
        (key,1)
      })
      .reduceByKey(_ + _)
      .map(x => {
        (x._1.split("_")(0),(x._1.split("_")(1), x._2))
      })
      .groupByKey()
      .map(x => {
        var list = mutable.MutableList[Tel]()
        x._2.foreach(y => {
          list += Tel(y._1, y._2)
        })
        PreferTel(x._1, list, System.currentTimeMillis())
      })
      .saveToEs("prefer_tel", writeConfig)


    // 统计今日订单消费金额
    payOrderEsRDD
      .map(x => {
        val doc = x._2("doc").asInstanceOf[mutable.Map[String, String]]
        (doc("username"), doc("orderTotal"))
      })
      .groupByKey()
      .map(x => {
        var orderTotal = 0f
        var payCnt = 0
        x._2.foreach(y => {
          orderTotal += y.toFloat
          payCnt += 1
        })
        PayOrder(x._1, orderTotal, payCnt, System.currentTimeMillis())
      })
      .saveToEs("pay_order", writeConfig)


    // 统计最多浏览类别
    getGoodDetailEsRDD
      .map(x => {
        val doc = x._2("doc").asInstanceOf[mutable.Map[String, String]]
        (doc("username"), doc("catName"))
      })
      .map(x => {
        val key = x._1 + "_" + x._2
        (key,1)
      })
      .reduceByKey(_ + _)
      .map(x => {
        (x._1.split("_")(0),(x._1.split("_")(1), x._2))
      })
      .groupByKey()
      .map(x => {
        var list = mutable.MutableList[Cat]()
        var cnt = 0
        x._2.foreach(y => {
          list += Cat(y._1, y._2)
          cnt += y._2
        })
        PreferCat(x._1, list, cnt, System.currentTimeMillis())
      })
      .saveToEs("prefer_cat", writeConfig)

    //  统计搜索关键词
    getGoodPageEsRDD
      .map(x => {
        val doc = x._2("doc").asInstanceOf[mutable.Map[String, String]]
        (doc("username"), doc("query"))
      })
      .map(x => {
        val key = x._1 + "_" + x._2
        (key,1)
      })
      .reduceByKey(_ + _)
      .map(x => {
        (x._1.split("_")(0),(x._1.split("_")(1), x._2))
      })
      .groupByKey()
      .map(x => {
        var list = mutable.MutableList[Query]()
        var cnt = 0
        x._2.foreach(y => {
          list += Query(y._1, y._2)
          cnt += y._2
        })
        LastMostQuery(x._1, list, cnt, System.currentTimeMillis())
      })
      .saveToEs("last_query", writeConfig)


    spark.stop()
  }
}
