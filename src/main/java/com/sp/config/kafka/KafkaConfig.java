package com.sp.config.kafka;


import com.sp.common.contants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.transaction.KafkaTransactionManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka 配置类
 * Created by admin on 2019/11/23.
 */
@Configuration
@EnableKafka
@Slf4j
public class KafkaConfig {

    /**
     * 创建 TopicName 为 topic.one，并设置分区数为8，副本数为1
     * @return
     */
    @Bean
    public NewTopic initialTopic() {
        return new NewTopic(Constants.TOPIC_TWO, 8, (short)1);
    }

    /**
     * 手动编码创建topic
     * @return
     */
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> props = new HashMap<>();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.1.121:9092");
        KafkaAdmin kafkaAdmin = new KafkaAdmin(props);
        return kafkaAdmin;
    }

    @Bean
    public AdminClient adminClient() {
        return AdminClient.create(kafkaAdmin().getConfig());
    }

    @Bean
    public KafkaTemplate<Integer, String> kafkaTemplate() {
        return new KafkaTemplate<Integer, String>(producerFactory());
    }

    /**
     * ConcurrentKafkaListenerContainerFactory为创建Kafka监听器的工厂类，这里只配置了消费者
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<Integer, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setReplyTemplate(kafkaTemplate());
        factory.setConcurrency(3);
        factory.getContainerProperties().setPollTimeout(3000);
        return factory;
    }

    /*@Bean
    public KafkaMessageListenerContainer listenerContainer() {
        ContainerProperties properties = new ContainerProperties(Constants.TOPIC_DEFAULT);
        properties.setGroupId(Constants.GROUPID_DEFAULT);

        properties.setMessageListener(new MessageListener<Integer, String>() {
            @Override
            public void onMessage(ConsumerRecord<Integer, String> data) {
                log.info("receive message:{}", data.toString());
            }
        });

        return new KafkaMessageListenerContainer(consumerFactory(), properties);
    }*/


    /**
     * 根据consumerProps填写的参数创建消费者工厂
     * @return
     */
    @Bean
    public ConsumerFactory<Integer, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerProps());
    }

    @Bean
    public ProducerFactory<Integer, String> producerFactory() {
        DefaultKafkaProducerFactory factory = new DefaultKafkaProducerFactory<>(senderProps());
        /*// 开启事务功能
        factory.transactionCapable();
        // 生成Transactional.id的前缀
        factory.setTransactionIdPrefix("tx-");*/
        return factory;
    }

    //@Bean
    public KafkaTransactionManager transactionManager() {
        KafkaTransactionManager manager = new KafkaTransactionManager(producerFactory());
        return manager;
    }

    //消费者配置参数
    private Map<String, Object> consumerProps() {
        Map<String, Object> props = new HashMap<>();
        //连接地址
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.1.121:9092");
        //GroupID
        props.put(ConsumerConfig.GROUP_ID_CONFIG, Constants.GROUPID_DEFAULT);
        //是否自动提交
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        //自动提交的频率
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
        //Session超时设置
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
        //一次拉取消息数量
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "5");
        //键的反序列化方式
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
        //值的反序列化方式
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }

    //生产者配置
    private Map<String, Object> senderProps (){
        Map<String, Object> props = new HashMap<>();
        //连接地址
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.1.121:9092");
        //重试，0为不启用重试机制
        props.put(ProducerConfig.RETRIES_CONFIG, 1);
        //控制批处理大小，单位为字节
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        //批量发送，延迟为1毫秒，启用该功能能有效减少生产者发送消息次数，从而提高并发量
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        //生产者可以使用的总内存字节来缓冲等待发送到服务器的记录
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 1024000);
        //键的序列化方式
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        //值的序列化方式
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }
}
