package com.sp.service.kafka;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

/**
 * Created by admin on 2019/12/2.
 */
@Slf4j
public class KafkaUtils {

    private static Producer<String, String> producer;

    private static KafkaConsumer<String, String> consumer;

    static {
        Properties producerProps = new Properties();
        //必需的3个参数
        producerProps.put("bootstrap.servers", "192.168.1.121:9092");
        producerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<>(producerProps);

        Properties consumerProps = new Properties();
        consumerProps.put("bootstrap.servers", "192.168.1.121:9092");
        consumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.put("group.id", "VoucherGroup");
        //关闭自动提交offset
        consumerProps.put("enable.auto.commit", "false");
        consumer = new KafkaConsumer<>(consumerProps);
    }

    /**
     * 同步发送消息
     *
     * @param topic topic
     * @param value 消息内容
     */
    public static void sendSync(String topic, String value) throws ExecutionException, InterruptedException {
        producer.send(new ProducerRecord<>(topic, value)).get();
    }

    /**
     * 消费消息
     *
     * @param c 回调函数，处理消息
     */
    public static void consume(Consumer<ConsumerRecord<String, String>> c) {
        consumer.subscribe(Lists.newArrayList(EventType.USER_CREATED.name()));
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                log.debug("接收到消息，ConsumerRecord={}", record);
                c.accept(record);
            }
            try {
                //同步手动提交offset
                consumer.commitSync();
            } catch (CommitFailedException e) {
                log.error("Kafka消费者提交offset失败", e);
            }
        }
    }
}
