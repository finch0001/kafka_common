package com.intest.kafka.common.demo

import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit
import java.util.{Properties, UUID}

import com.intest.kafka.common.compress.ZstdCompressor
import com.intest.kafka.common.demo.Avro4sDemo.Pizza
import com.intest.kafka.common.producer.KafkaProducerWrapper
import com.sksamuel.avro4s.{AvroSchema, RecordFormat, SchemaFor}
import com.sksamuel.avro4s.kafka.GenericSerde
import com.twitter.bijection.Injection
import com.twitter.bijection.avro.GenericAvroCodecs
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}

import scala.collection.mutable

/**
 * @author yusheng
 * @datetime 2020/7/14 8:29
 **/
object ShevCarDataProducer {

  // case class DiBiao(id:Int,values:Seq[Double])
  //case class CarData(vin:String,collectionTime:Long,diBiaoMap:mutable.HashMap[String,DiBiao])
  case class Location(lat:Double,lon:Double)
  case class CarData(vin:String,id:Int,values:Seq[Location])
  // case class CarData(vin:String,id:Int,values:Seq[Double],location: Location)



  def main(args:Array[String]): Unit ={

    // kafka配置参数
    val producerProps = new Properties()
    producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "namenode:9092")
    producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer")
    producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArraySerializer")
    // producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, new GenericSerde[CarData]())
    producerProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG,"lz4")

    //创建一个kafka生产者
    val producer: KafkaProducer[String, Array[Byte]] = new KafkaProducer(producerProps)

    val producerWrapper = new KafkaProducerWrapper[String,Array[Byte]](producer)

    // implicit val schemaFor = SchemaFor[CarData]
    val format = RecordFormat[CarData]

    val schema = AvroSchema[CarData]
    val recordInjection: Injection[GenericRecord, Array[Byte]] = GenericAvroCodecs.toBinary(schema)

    val zStdCompressor = ZstdCompressor.getOrCreate(ZstdCompressor.DEFAULT_COMPRESSION_LEVEL)

    var batchNum = 0
    for(i <- 0 until 300000){
      val key = UUID.randomUUID().toString

      // val diBiaoMap = new scala.collection.mutable.HashMap[String,DiBiao]()
      // diBiaoMap.put("dibiao-1",DiBiao(1,Seq(1.8d,2.09d,3.04d,2.98d)))
      // diBiaoMap.put("dibiao-2",DiBiao(2,Seq(1.8d,2.09d,3.04d,2.98d)))
      // diBiaoMap.put("dibiao-3",DiBiao(3,Seq(1.8d,2.09d,3.04d,2.98d)))

      // val row = CarData(key,System.currentTimeMillis(),diBiaoMap)
      // val row = CarData(key,i,Seq(3.04d,4.5d,0.9d,1.32d),Location(4.04d,12.03d))
      val row = CarData(key,i,Seq(Location(4.03d,3.0d),Location(0.1d,3.03d)))
      val record = format.to(row)
      val bytes = recordInjection.apply(record)

      // val inBuffer = ByteBuffer.allocateDirect(bytes.length)
      // val outBuffer = ByteBuffer.allocateDirect(bytes.length)

      // zStdCompressor.compress(inBuffer,outBuffer)

      // val compressBytes = new Array[Byte](outBuffer.position())
      // outBuffer.position(0)
      // outBuffer.get(compressBytes)
      // println(compressBytes.length)

      // val producerRecord = new ProducerRecord[String, Array[Byte]]("ys_mdf4_test", bytes)
      val producerRecord = new ProducerRecord[String, Array[Byte]]("ys_mdf4_test_v2", bytes)

      val future = producer.send(producerRecord)

      // producerWrapper.sendSynchronously()

      val metaData = future.get()
      println("id:" + i + ", offset:" + metaData.offset() + ", timestamp:" + metaData.timestamp())

      if(batchNum == 500) {
        TimeUnit.SECONDS.sleep(1)
        batchNum = 0
      }

      batchNum += 1
    }




    producerWrapper.close()
  }

}
