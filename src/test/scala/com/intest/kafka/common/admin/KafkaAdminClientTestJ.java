package com.intest.kafka.common.admin;

import com.intest.kafka.common.examples.KafkaProperties;
import kafka.admin.AdminClient;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.Node;
import org.hamcrest.core.CombinableMatcher;

import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author yusheng
 * @datetime 2020/7/13 9:49
 */
public class KafkaAdminClientTestJ {

    public static void main(String[] args){
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaProperties.KAFKA_SERVER_URL + ":" + KafkaProperties.KAFKA_SERVER_PORT);
        props.put("zookeeper.connect","namenode:2181");
        // props.put(ConsumerConfig.GROUP_ID_CONFIG, "DemoConsumer");
        // props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        // props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        // props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        // props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.IntegerDeserializer");
        // props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        CommonAdminClient adminClient = new CommonAdminClient(props);

        // adminClient.createTopic("ys_test01",3,1);
        // adminClient.deleteTopic("ys_test01");

        for (String topic : adminClient.getTopics()) {
            System.out.println(topic);
        }

        /*
        System.out.println(adminClient.getTopicConfig("ys_mdf4_test"));

        AdminClient admin = adminClient.getAdminClient();
        admin.findAllBrokers().toList();

        Set<String> nodes = new HashSet<>();
        nodes.add("01");
        nodes.add("03");
        nodes.add("02");
        scala.collection.immutable.Set<String> tmp  = CommonAdminClient.toImmutableScalaSet(nodes);
        // tmp.toList().foreach(f -> System.out.println(f));
        */

        adminClient.close();

    }

}
