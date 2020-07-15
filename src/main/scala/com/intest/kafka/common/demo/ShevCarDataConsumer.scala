package com.intest.kafka.common.demo

import java.util.{Collections, Properties}

import com.intest.kafka.common.consumer.{ProcessingConfig, ProcessingKafkaConsumer}
import com.intest.kafka.common.demo.ShevCarDataProducer.CarData
import com.sksamuel.avro4s.{AvroInputStream, AvroSchema}
import com.twitter.bijection.Injection
import com.twitter.bijection.avro.GenericAvroCodecs
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.consumer.ConsumerRecords

import scala.collection.JavaConversions._

/**
 * @author yusheng
 * @datetime 2020/7/14 8:48
 **/
object ShevCarDataConsumer {

  def main(args:Array[String]): Unit ={


    val consumerProps = new Properties()
    consumerProps.put("bootstrap.servers", "namenode:9092")
    consumerProps.put("group.id", "G1") // 消费组ID
    consumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    consumerProps.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer")
    consumerProps.put("auto.offset.reset","latest") // latest / earliest / none
    val processingConfig = new ProcessingConfig(consumerProps)

    val kafkaConsumer = new ProcessingKafkaConsumer[String,Array[Byte]](processingConfig)

    kafkaConsumer.subscribe(Collections.singletonList("ys_mdf4_test"))

    val schema = AvroSchema[CarData]
    // val recordInjection: Injection[GenericRecord, Array[Byte]] = GenericAvroCodecs.toBinary(schema)

    try{
      val consumer = kafkaConsumer.getConsumer
      while(true){
        val consumerRecords: ConsumerRecords[String, Array[Byte]]  = consumer.poll(100)

        for (record <- consumerRecords) {

          //  每条记录都包含了记录所属主题的信息、记录所在分区的信息、记录在分区里的偏移量，以及记录的键值对
          // val genericRecord: GenericRecord = recordInjection.invert(record.value()).get
          // println(genericRecord)

          // val is = AvroInputStream.data[CarData].from(record.value()).build(schema)
          // is.iterator.toSeq.foreach(x => println(x))
          val is = AvroInputStream.binary[CarData].from(record.value()).build(schema)
          is.iterator.toSeq.foreach(x => println(x))


        }
      }

      // 同步提交 ：在broker对提交请求做出回应之前，应用会一直阻塞
      // 处理完当前批次的消息，在轮询更多的消息之前，
      // 调用 commitSync() 方法提交当前批次最新的偏移量
      consumer.commitAsync()
    }catch {
      case ex: Throwable => {
        ex.printStackTrace()
      }
    }

    kafkaConsumer.close()
  }


}
