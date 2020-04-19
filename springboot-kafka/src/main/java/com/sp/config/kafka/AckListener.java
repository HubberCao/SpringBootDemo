package com.sp.config.kafka;

import com.sp.bean.model.EventProcess;
import com.sp.common.contants.Constants;
import com.sp.common.enums.EventProcessStatus;
import com.sp.common.enums.EventType;
import com.sp.dao.EventProcessDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Ack机制确认消费
 * Created by admin on 2019/11/24.
 */
//@Component
@Slf4j
public class AckListener {

    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;

    @Autowired
    private EventProcessDao eventProcessDao;

    private Map<String, Object> consumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.1.121:9092");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }

    @Bean("ackContainerFactory")
    public ConcurrentKafkaListenerContainerFactory ackContainerFactory() {
        ConcurrentKafkaListenerContainerFactory factory = new ConcurrentKafkaListenerContainerFactory();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory(consumerProps()));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        //设置并发量，小于或等于Topic的分区数
        factory.setConcurrency(5);
        //设置为批量监听
        factory.setBatchListener(true);

        return factory;
    }

    //@KafkaListener(id = "ack", topics = Constants.TOPIC_ACK, containerFactory = "ackContainerFactory")
    public void ackListener(ConsumerRecord record, Acknowledgment ack, Consumer consumer) {
        log.info("topic.ack receive:{}, offset:{}", record.value(), record.offset());
        //如果偏移量为偶数则确认消费，否则拒绝消费
        if (record.offset() % 2 == 0) {
            log.info(record.offset()+"--ack");
            ack.acknowledge();
        } else {
            log.info(record.offset()+"--nack");
            // 重新将消息发送到队列
            kafkaTemplate.send(Constants.TOPIC_ACK, record.value().toString());

            // 使用Consumer.seek方法，重新回到该未ack消息偏移量的位置重新消费，这种可能会导致死循环，原因出现于业务一直没办法处理这条数据，但还是不停的重新定位到该数据的偏移量上。
            // consumer.seek(new TopicPartition("topic.quick.ack",record.partition()),record.offset() );
        }
        //ack.acknowledge();
    }

    //@KafkaListener(id = "VoucherGroup", topics = "USER_CREATED", containerFactory = "ackContainerFactory")
    public void ackComsumer(ConsumerRecord record, Acknowledgment ack) {

        log.info("ackComsumer 接收到消息，ConsumerRecord={}", record.value());

        int i=0;
        //ack.acknowledge();
    }

    @KafkaListener(id = "VoucherGroup", topics = "USER_CREATED", containerFactory = "ackContainerFactory")
    public void batchAckComsumer(ConsumerRecords<String, String> records, Acknowledgment ack) {
        log.info("本次接收{}条消息", records.count());
        for (ConsumerRecord<String, String> record : records) {
            log.info("batch 接收到消息，ConsumerRecord={}", record);
            EventProcess eventProcess = new EventProcess();
            eventProcess.setPayload(record.value());
            eventProcess.setEventType(EventType.USER_CREATED);
            eventProcess.setStatus(EventProcessStatus.NEW);
            eventProcessDao.save(eventProcess);
        }

        ack.acknowledge();
        log.info("成功入库");
    }

}
