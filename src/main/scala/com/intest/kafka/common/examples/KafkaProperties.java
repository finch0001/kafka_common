package com.intest.kafka.common.examples;

public class KafkaProperties {
    public static final String TOPIC = "ys_mdf4_test";
    public static final String KAFKA_SERVER_URL = "namenode";
    public static final int KAFKA_SERVER_PORT = 9092;
    public static final int KAFKA_PRODUCER_BUFFER_SIZE = 64 * 1024;
    public static final int CONNECTION_TIMEOUT = 100000;
    public static final String TOPIC2 = "topic2";
    public static final String TOPIC3 = "topic3";
    public static final String CLIENT_ID = "SimpleConsumerDemoClient";

    private KafkaProperties() {}
}