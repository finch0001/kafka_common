package com.intest.kafka.common.demo

import java.util.{Properties, UUID}

import com.intest.kafka.common.demo.Avro4sDemo.Pizza
import com.intest.kafka.common.producer.KafkaProducerWrapper
import com.sksamuel.avro4s.{AvroSchema, RecordFormat}
import com.sksamuel.avro4s.kafka.GenericSerde
import com.twitter.bijection.Injection
import com.twitter.bijection.avro.GenericAvroCodecs
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}

/**
 * @author yusheng
 * @datetime 2020/7/14 8:29
 **/
object ShevCarDataProducer {

  case class CarData(vin:String,collectionTime:Long)

  def main(args:Array[String]): Unit ={

    // kafka配置参数
    val producerProps = new Properties()
    producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "namenode:9092")
    producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer")
    producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArraySerializer")
    // producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, new GenericSerde[CarData]())

    //创建一个kafka生产者
    val producer: KafkaProducer[String, Array[Byte]] = new KafkaProducer(producerProps)

    val producerWrapper = new KafkaProducerWrapper[String,Array[Byte]](producer)

    val format = RecordFormat[CarData]

    val schema = AvroSchema[CarData]
    val recordInjection: Injection[GenericRecord, Array[Byte]] = GenericAvroCodecs.toBinary(schema)

    for(i <- 0 until 1000){
      val key = UUID.randomUUID().toString
      val row = CarData(key,System.currentTimeMillis())
      val record = format.to(row)
      val bytes = recordInjection.apply(record)
      val producerRecord = new ProducerRecord[String, Array[Byte]]("ys_mdf4_test", bytes)
      val future = producer.send(producerRecord)

      // producerWrapper.sendSynchronously()

      val metaData = future.get()
      println("offset:" + metaData.offset())
      println("partition:" + metaData.partition())
    }




    producerWrapper.close()
  }

}
