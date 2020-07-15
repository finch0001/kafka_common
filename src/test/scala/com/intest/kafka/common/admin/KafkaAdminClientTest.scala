package com.intest.kafka.common.admin

import java.util
import java.util.Properties

import com.intest.kafka.common.examples.KafkaProperties
import org.apache.kafka.clients.consumer.ConsumerConfig

/**
 * @author yusheng
 * @datetime 2020/7/13 10:24
 **/
object KafkaAdminClientTest {

  def main(args:Array[String]): Unit ={
    val props = new Properties();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaProperties.KAFKA_SERVER_URL + ":" + KafkaProperties.KAFKA_SERVER_PORT);
    props.put("zookeeper.connect","namenode:2181");
    val kafkaAdminClient = new CommonAdminClient(props)

    val admin = kafkaAdminClient.getAdminClient

    admin.bootstrapBrokers.foreach(x => {
      println(x)
    })

    val nodes = new util.HashSet[String]();
    nodes.add("01");
    nodes.add("03");
    nodes.add("02");
    val tmp  = CommonAdminClient.toImmutableScalaSet(nodes);
    tmp.foreach(println)


    kafkaAdminClient.close()
  }

}
