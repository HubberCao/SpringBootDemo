package com.sp.config.kafka;

import com.sp.common.contants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.messaging.handler.annotation.SendTo;

/**
 * 实现消息转发
 *  1. @SendTo
 *  2. ReplyingKafkaTemplate
 * Created by admin on 2019/11/24.
 */
@Configuration
@Slf4j
public class ForwardListener {

    @Autowired
    private ProducerFactory producerFactory;

    @Autowired
    private ConsumerFactory consumerFactory;


    @KafkaListener(id = "forward", topics = Constants.TOPIC_TARGET)
    @SendTo(Constants.TOPIC_REAL)
    public String forward(String data) {
        log.info("topic.target forward {}  to topic.real", data);
        return "topic.target send msg: " + data;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Integer, String> replyContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setReplyTemplate(replyingKafkaTemplate());
        factory.setBatchListener(false);
        factory.getContainerProperties().setPollTimeout(1000);
        factory.getContainerProperties().setIdleEventInterval(10000L);
        factory.setConcurrency(3);
        return factory;
    }

    @Bean
    public KafkaMessageListenerContainer replyContainer() {
        ContainerProperties containerProperties = new ContainerProperties(Constants.TOPIC_REPLY);
        return new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
    }

    @Bean
    public ReplyingKafkaTemplate<String, String, String> replyingKafkaTemplate() {
        ReplyingKafkaTemplate template = new ReplyingKafkaTemplate<>(producerFactory, replyContainer());
        template.setReplyTimeout(10000);
        template.setSharedReplyTopic(true);
        return template;
    }

    @KafkaListener(id = "replyConsumer", topics = Constants.TOPIC_REQUEST, containerFactory = "replyContainerFactory")
    @SendTo
    public String replyListen(String msgData){
        log.info("topic.request receive : {}", msgData);
        return "topic.reply  reply : " + msgData;
    }

}
