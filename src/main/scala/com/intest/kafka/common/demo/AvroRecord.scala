package com.intest.kafka.common.demo

import java.io.File

import com.twitter.bijection.Injection
import com.twitter.bijection.avro.GenericAvroCodecs
import org.apache.avro.Schema
import org.apache.avro.generic.{GenericData, GenericRecord, GenericRecordBuilder}

/**
 * @author yusheng
 * @datetime 2020/7/13 15:40
 **/
object AvroRecord {
  case class Vehicledata(vehiclestatus:Int,
                         chargestatus:Int,
                         runmodel:Int,
                         speed:Double,
                         summileage:Double,
                         sumvoltage:Double,
                         sumcurrent:Double,
                         soc:Int,
                         dcdcstatus:Int,
                         gearnum:Int,
                         havedriver:Int,
                         havebrake:Int,
                         insulationresistance:Int,
                         acceleratorpedal:Int,
                         brakestatus:Int)


  def main(args:Array[String]): Unit ={

    // Avro Schema解析
    val avscFile = "E:\\work\\project\\java\\kafak_common\\src\\main\\resources\\avro\\shev.avsc"
    val schema: Schema = new Schema.Parser().parse(new File(avscFile))

    println(schema)

    val recordInjection: Injection[GenericRecord, Array[Byte]] = GenericAvroCodecs.toBinary(schema)

    // val recordBuilder = new GenericRecordBuilder(schema)
    // recordBuilder.set("vin","f923m40as1")
    // recordBuilder.set("collectiontime",1594626253748L)
    // val avroRecord = recordBuilder.build()

    val avroRecord = new GenericData.Record(schema)
    avroRecord.put("vin","f923m40as1")
    avroRecord.put("collectiontime",1594626253748L)
    // avroRecord.put("shev.car.vehicledata.vehiclestatus",1)
    // avroRecord.put("vehicledata.speed",55.03d)
    val vehicleData = Vehicledata(0,0,0,0d,0d,0d,0d,0,0,0,0,0,0,0,0)


    avroRecord.put("vehicledata",vehicleData)


    println(avroRecord)

    // val bytes = recordInjection.apply(avroRecord)


  }


}
