package com.sp.config.kafka;

import com.sp.common.contants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

import java.util.List;

/**
 * 批量监听
 * Created by admin on 2019/11/25.
 */
//@Component
@Slf4j
public class BatchListener {

    @Autowired
    private ConsumerFactory consumerFactory;

    @Bean("batchContainerFactory")
    public ConcurrentKafkaListenerContainerFactory batchContainerFactory() {
        ConcurrentKafkaListenerContainerFactory container = new ConcurrentKafkaListenerContainerFactory();
        container.setConsumerFactory(consumerFactory);
        //设置并发量，小于或等于Topic的分区数
        container.setConcurrency(5);
        //设置为批量监听
        container.setBatchListener(true);
        return container;
    }

    @Bean
    public NewTopic batchTopic() {
        return new NewTopic("topic.batch", 8, (short) 1);
    }


    @KafkaListener(id = "batch",clientIdPrefix = "batch",topics = {Constants.TOPIC_BATCH},containerFactory = "batchContainerFactory")
    public void batchListener(List<String> data) {
        log.info("topic.batch  receive : ");
        for (String s : data) {
            log.info("data:{}", s);
        }
    }
}
