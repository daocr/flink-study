package com.huilong.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

/**
 * kafka 消费者
 *
 * @author daocr
 * @date 2020/11/16
 */
public class MyKafkaConsumer<K, V> {

    private KafkaConsumer<K, V> consumer;

    public MyKafkaConsumer(String servers, String groupId, List<String> topicList, String keyDeserializer, String valueDeserializer, Consumer<ConsumerRecord<K, V>> callBack) {

        Properties props = new Properties();
        props.put("bootstrap.servers", servers);
        //消费者的组id
        props.put("group.id", groupId);
        //用于配置是否自动的提交消费进度
        props.put("enable.auto.commit", "true");
        //用于配置自动提交消费进度的时间；
        props.put("auto.commit.interval.ms", "1000");
        // 会话超时时长，客户端需要周期性的发送“心跳”到broker，这样broker端就可以判断消费者的状态，如果消费在会话周期时长内未发送心跳，那么该消费者将被判定为dead，那么它之前所消费的partition将会被重新的分配给其他存活的消费者
        props.put("session.timeout.ms", "30000");
        // 解码
        props.put("key.deserializer", keyDeserializer);
        props.put("value.deserializer", valueDeserializer);

        KafkaConsumer<K, V> consumer = new KafkaConsumer<K, V>(props);

        //订阅主题列表topic
        consumer.subscribe(topicList);

    }

    /**
     * 开始监听
     *
     * @param callBack
     */
    public void monitoring(Consumer<ConsumerRecord<K, V>> callBack) {

        while (true) {
            ConsumerRecords<K, V> records = (ConsumerRecords<K, V>) consumer.poll(Duration.ofMillis(100));
            records.forEach(callBack::accept);
        }
    }
}
