package com.intest.kafka.common.admin

import org.apache.avro.Schema

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path

import org.apache.parquet.avro.AvroSchemaConverter
import org.apache.parquet.hadoop.ParquetFileReader
import org.apache.parquet.hadoop.util.HadoopInputFile

object ParquetToAvroSchemaConverter {
  def main(args: Array[String]): Unit = {
    val path = new Path("hdfs://namenode:8020/5703/20190411/part-00000-50beac1e-acd1-4564-b706-62c38c2f3541-c000.snappy.parquet")
    val avroSchema = convert(path)
  }

  def convert(parquetPath: Path): Schema = {
    val cfg = new Configuration()
    // Create parquet reader

    // val rdr = ParquetFileReader.open(HadoopInputFile.fromPath(parquetPath, cfg))
    val rdr = ParquetFileReader.open(cfg,parquetPath)


    try {
      // Get parquet schema
      val schema = rdr.getFooter.getFileMetaData.getSchema
      println("Parquet schema: ")
      println("#############################################################")
      print(schema.toString)
      println("#############################################################")
      println

      // Convert to Avro
      val avroSchema = new AvroSchemaConverter(cfg).convert(schema)
      println("Avro schema: ")
      println("#############################################################")
      println(avroSchema.toString(true))
      println("#############################################################")

      avroSchema
    }
    finally {
      rdr.close()
    }
  }
}