package com.intest.kafka.common.demo

import java.util.Properties

import com.sksamuel.avro4s.kafka.GenericSerde
import com.sksamuel.avro4s.{AvroSchema, BinaryFormat, RecordFormat}
import com.twitter.bijection.Injection
import com.twitter.bijection.avro.GenericAvroCodecs
import org.apache.avro.generic.{GenericData, GenericRecord}
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.producer.ProducerConfig

/**
 * @author yusheng
 * @datetime 2020/7/13 17:17
 **/
object Avro4sDemo {

  case class Ingredient(name: String, sugar: Double, fat: Double)
  case class Pizza(name: String, ingredients: Seq[Ingredient], vegetarian: Boolean, vegan: Boolean, calories: Int)

  def main(args:Array[String]): Unit ={

    val schema = AvroSchema[Pizza]
    println(schema)

    val ingredient = Ingredient("xxx",1.07d,0.45d)
    val pizza = Pizza("yyy",Seq(ingredient),true,false,1)
    val format = RecordFormat[Pizza]
    val record = format.to(pizza)
    println(record)

    val producerProps = new Properties();
    producerProps.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "...")
    // producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, new GenericSerde[Pizza](BinaryFormat))
    // producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, new GenericSerde[Pizza](BinaryFormat))
    producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,new GenericSerde[Pizza]())
    producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, new GenericSerde[Pizza]())
    // new ProducerConfig(producerProps)

    val recordInjection: Injection[GenericRecord, Array[Byte]] = GenericAvroCodecs.toBinary(schema)
    val bytes = recordInjection.apply(record)



  }

}
