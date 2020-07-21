package com.intest.kafka.common.demo

import java.nio.ByteBuffer

import com.intest.kafka.common.compress.ZstdCompressor
import com.intest.kafka.common.demo.ShevCarDataProducer.CarData
import com.sksamuel.avro4s.{AvroInputStream, AvroSchema}
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
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
    // sparkConf.set("spark.streaming.backpressure.enabled","true")
    // sparkConf.set("spark.streaming.backpressure.initialRate","1")
    // sparkConf.set("spark.streaming.kafka.maxRatePerPartition","1")
    sparkConf.registerKryoClasses(Array(classOf[ZstdCompressor]))

    val sc = new SparkContext(sparkConf)
    val ssc = new StreamingContext(sc,Seconds(1))

    val topics = Array("ys_mdf4_test_v2")

    val stream = KafkaUtils.createDirectStream[String, Array[Byte]](
      ssc,
      PreferConsistent,
      Subscribe[String, Array[Byte]](topics, kafkaParams)
    )

    val zStdCompressor = ZstdCompressor.getOrCreate(ZstdCompressor.DEFAULT_COMPRESSION_LEVEL)

    // val schema = AvroSchema[CarData]

    // stream.map(record => (record.key, record.value))
    stream.foreachRDD(rdd => {
      rdd.foreach(x => {
        val inBytes = x.value()
        // val intBuffer = ByteBuffer.allocateDirect(inBytes.length)
        // intBuffer.put(inBytes)
        // intBuffer.position(0)

        // val outBuffer = ByteBuffer.allocateDirect(inBytes.length)
        // zStdCompressor.uncompress(intBuffer,outBuffer)

        // val outPutBytes = new Array[Byte](1000)

        // val len = zStdCompressor.uncompress(inBytes,0,inBytes.length,outPutBytes,0)
        // println("content len: " + len)

        // val result = outPutBytes.slice(0,len)

        // val unCompressBytes = new Array[Byte](inBytes.length)
        // outBuffer.position(0)
        // outBuffer.get(unCompressBytes)

        val schema = AvroSchema[CarData]
        val is = AvroInputStream.binary[CarData].from(inBytes).build(schema)
        // val is = AvroInputStream.binary[CarData].from(x.value()).build(schema)
        is.iterator.toSeq.foreach(x => println(x))
      })
    })

    ssc.start()
    ssc.awaitTermination()

  }

}
