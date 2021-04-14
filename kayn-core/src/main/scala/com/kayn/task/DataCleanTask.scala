package com.kayn.task

import java.util.concurrent.{Executors, TimeUnit}

import com.mongodb.casbah.Imports.{MongoClient, MongoClientURI, MongoDBObject}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}
import org.elasticsearch.spark.sparkContextFunctions

import scala.collection.mutable


case class Result(
                   method: String,
                   doc: scala.collection.mutable.Map[String, String],
                   timestamp: Long
                 )

case class GetGoodPage(
                        query: String,
                        username: String
                      )

case class GetGoodDetail(
                          productId: Long,
                          username: String
                        )

case class AddOrder(
                     username: String,
                     orderTotal: Double,
                     tel: String,
                     street: String
                   )

case class PayOrder(
                     orderId: Long,
                     orderTotal: Double,
                     username: String
                   )

/**
 * MongoDB连接配置
 * @param uri 连接url
 * @param db 要操作的db
 */
case class MongoConfig(uri: String, db: String)

object DataCleanTask {

  //定义mongodb中存储的表名
  val COLLECTION_GET_GOOD_PAGE = "action_getGoodPage"
  val COLLECTION_GET_GOOD_DETAIL = "action_getGoodDetail"
  val COLLECTION_ADD_ORDER = "action_addOrder"
  val COLLECTION_PAY_ORDER = "action_payOrder"

  // 数据清洗，分类存储为List
  var GetGoodPageList: mutable.MutableList[GetGoodPage] = mutable.MutableList[GetGoodPage]()
  val GetGoodDetailList: mutable.MutableList[GetGoodDetail] = mutable.MutableList[GetGoodDetail]()
  val AddOrderList: mutable.MutableList[AddOrder] = mutable.MutableList[AddOrder]()
  val PayOrderList: mutable.MutableList[PayOrder] = mutable.MutableList[PayOrder]()

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
    val config = Map(
      "mongo.uri" -> "mongodb://localhost:27017/UserAction",
      "mongo.db" -> "UserAction",
      "es.nodes" -> "localhost",
      "es.port" -> "9200",
      "es.index.auto.create" -> "true",
      "es.index.read.missing.as.empty" -> "true",
      "es.RDD" -> "kafka-log-2021.04.14"
    )

    //创建spark config
    val sparkConf = new SparkConf().setAppName("ES2Spark").setMaster("local[*]")
    sparkConf.set("es.nodes", config("es.nodes"))
    sparkConf.set("es.port", config("es.port"))
    sparkConf.set("es.index.auto.create", config("es.index.auto.create"))
    sparkConf.set("es.index.read.missing.as.empty", config("es.index.read.missing.as.empty"))

    val EsRDD = new SparkContext(sparkConf).esRDD(config("es.RDD"))

    //创建spark session
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()
    import spark.implicits._

    EsRDD.map(item => {
      val data = item._2
      val source: mutable.Map[String, String] = data("source").asInstanceOf[mutable.Map[String, String]]

      // 时间格式化
      import java.util.Date
      val timestamp = data("@timestamp").asInstanceOf[Date].getTime
      println(timestamp - (System.currentTimeMillis() - 60 * 1000))

      if (data.contains("doc")) {
        val doc: mutable.Map[String, String] = data("doc").asInstanceOf[mutable.Map[String, String]]
        Result(source("method"), doc, timestamp)
      } else {
        Result("system", mutable.Map[String, String](), timestamp)
      }
    }).filter(item => {
      // 过滤1分钟以前的日志
      item.timestamp > (System.currentTimeMillis() - 60 * 1000)
    }).foreach(item => {
      if (item.method == "getGoodPage") {
        GetGoodPageList += GetGoodPage(item.doc("query"), item.doc("username"))
      } else if (item.method == "getGoodDetail") {
        GetGoodDetailList += GetGoodDetail(item.doc("productId").toLong, item.doc("username"))
      } else if (item.method == "addOrder") {
        AddOrderList += AddOrder(item.doc("username"), item.doc("orderTotal").toDouble, item.doc("tel"), item.doc("street"))
      } else if (item.method == "payOrder") {
        PayOrderList += PayOrder(item.doc("orderId").toLong, item.doc("orderTotal").toDouble, item.doc("username"))
      }
    })

    //作为下面方法的隐式参数，免得每次都调用
    implicit val mongoConfig: MongoConfig = MongoConfig(config("mongo.uri"), config("mongo.db"))

    //存入MongoDB
    storeDataInMongoDB(GetGoodPageList.toDF(), COLLECTION_GET_GOOD_PAGE)
    storeDataInMongoDB(GetGoodDetailList.toDF(), COLLECTION_GET_GOOD_DETAIL)
    storeDataInMongoDB(AddOrderList.toDF(), COLLECTION_ADD_ORDER)
    storeDataInMongoDB(PayOrderList.toDF(), COLLECTION_PAY_ORDER)

    spark.stop()
  }

  def storeDataInMongoDB(actionDF: DataFrame, collection: String)(implicit mongoConfig: MongoConfig): Unit ={

    //新建一个mongodb的连接
    val mongoClient = MongoClient(MongoClientURI(mongoConfig.uri))

    //定义要操作的表
    val productCollection = mongoClient(mongoConfig.db)(collection)

    //如果表存在，则删掉
    productCollection.dropCollection()

    //将当前数据存入对应的表中
    actionDF.write
      .option("uri", mongoConfig.uri)
      .option("collection", collection)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()

    //对表创建索引
    productCollection.createIndex(MongoDBObject("id" -> 1))

    mongoClient.close()
  }
}
