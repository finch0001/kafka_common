package com.intest.kafka.common.demo

import com.intest.kafka.common.schema.{DatabricksSchemaConverters, SchemaConverters}
import org.apache.avro.SchemaBuilder
import org.apache.spark.sql.SparkSession

/**
 * @author yusheng
 * @datetime 2020/7/13 15:09
 **/
object SparkSchemaAvroConverter {

  def main(args:Array[String]): Unit ={
    val sparkSession = SparkSession.builder()
      .master("local[2]")
      .appName("SparkSchemaAvroConverter")
      .getOrCreate()

    val parquetFile = "E:\\work\\project\\shev_cardata_quality_detect\\数据\\Parquet原始数据\\新建文件夹\\shev_test_5996_20200607.parquet"
    val df = sparkSession.read.parquet(parquetFile)
    val parquetSchema = df.schema

    val schema = SchemaConverters.toAvroType(parquetSchema,true,"car","shev.detect")
    println(schema)


    sparkSession.stop()
  }

}
