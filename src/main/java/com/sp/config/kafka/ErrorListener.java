package com.sp.config.kafka;

import com.sp.common.contants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.listener.ConsumerAwareListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 异常处理器
 * Created by admin on 2019/11/24.
 */
@Component
@Slf4j
public class ErrorListener {

    /*@KafkaListener(id = "err", topics = Constants.TOPIC_ERROR, errorHandler = "consumerAwareErrorHandler")
    public void errorListener(String data) {
        log.info("topic.error  receive : " + data);
        throw new RuntimeException("fail");
    }

    *//**
     * 单消费异常处理器
     * @return
     *//*
    @Bean
    public ConsumerAwareListenerErrorHandler consumerAwareErrorHandler() {
        return new ConsumerAwareListenerErrorHandler() {
            @Override
            public Object handleError(Message<?> message, ListenerExecutionFailedException exception, Consumer<?, ?> consumer) {
                log.info("error handler receive:{}", message.getPayload().toString());
                return null;
            }
        };
    }*/

    @KafkaListener(id = "err", topics = Constants.TOPIC_ERROR, errorHandler = "batchConsumerAwareErrorHandler")
    public void batchErrorListener(String data) {
        log.info("topic.error  receive : " + data);
        throw new RuntimeException("fail");
    }

    /**
     * 批量消费异常处理器
     * @return
     */
    @Bean
    public ConsumerAwareListenerErrorHandler batchConsumerAwareErrorHandler() {
        return new ConsumerAwareListenerErrorHandler() {

            @Override
            public Object handleError(Message<?> message, ListenerExecutionFailedException e, Consumer<?, ?> consumer) {
                log.info("consumerAwareErrorHandler receive : "+message.getPayload().toString());
                MessageHeaders headers = message.getHeaders();
                List<String> topics = headers.get(KafkaHeaders.RECEIVED_TOPIC, List.class);
                List<Integer> partitions = headers.get(KafkaHeaders.RECEIVED_PARTITION_ID, List.class);
                List<Long> offsets = headers.get(KafkaHeaders.OFFSET, List.class);
                Map<TopicPartition, Long> offsetsToReset = new HashMap<>();

                return null;
            }
        };
    }
}
