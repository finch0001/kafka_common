package com.intest.kafka.common.demo

import com.intest.kafka.common.demo.ShevCarDataProducer.CarData
import com.sksamuel.avro4s.{AvroInputStream, AvroSchema}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

/**
 * @author yusheng
 * @datetime 2020/7/20 9:05
 **/
object SparkStreamingConsumer {

  def main(args:Array[String]): Unit ={

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "namenode:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> "org.apache.kafka.common.serialization.ByteArrayDeserializer",
      "group.id" -> "use_a_separate_group_id_for_each_stream",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val sparkConf = new SparkConf().setMaster("local[2]").setAppName("App")
    val sc = new SparkContext(sparkConf)
    val ssc = new StreamingContext(sc,Seconds(1))

    val topics = Array("ys_mdf4_test")

    val stream = KafkaUtils.createDirectStream[String, Array[Byte]](
      ssc,
      PreferConsistent,
      Subscribe[String, Array[Byte]](topics, kafkaParams)
    )

    // val schema = AvroSchema[CarData]

    // stream.map(record => (record.key, record.value))
    stream.foreachRDD(rdd => {
      rdd.foreach(x => {

        val schema = AvroSchema[CarData]
        val is = AvroInputStream.binary[CarData].from(x.value()).build(schema)
        is.iterator.toSeq.foreach(x => println(x))



      })
    })

    ssc.start()
    ssc.awaitTermination()

  }

}
