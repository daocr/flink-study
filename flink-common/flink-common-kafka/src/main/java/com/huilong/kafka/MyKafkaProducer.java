package com.huilong.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.Properties;

/**
 * kafka 生产者
 *
 * @author daocr
 * @date 2020/11/16
 */
public class MyKafkaProducer<K, V> {

    private KafkaProducer<K, V> kafkaProducer;

    public MyKafkaProducer(String servers, String valueSerializer, String keySerializer) {

        Properties properties = new Properties();
        properties.put("bootstrap.servers", servers);
        properties.put("value.serializer", valueSerializer);
        properties.put("key.serializer", keySerializer);

        // properties.put("value.serializer",  "org.apache.kafka.common.serialization.ByteArraySerializer");
        // properties.put("key.serializer",  "org.apache.kafka.common.serialization.ByteArraySerializer");

//        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");


        kafkaProducer = new KafkaProducer<K, V>((properties));

    }


}
