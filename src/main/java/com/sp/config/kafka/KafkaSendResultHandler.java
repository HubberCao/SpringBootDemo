package com.sp.config.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.stereotype.Component;

/**
 * 消息结果回调类
 * Created by admin on 2019/11/24.
 */
@Component
@Slf4j
public class KafkaSendResultHandler implements ProducerListener {
    @Override
    public void onSuccess(ProducerRecord producerRecord, RecordMetadata recordMetadata) {
        log.info("message send success:{}", producerRecord.toString());
    }

    @Override
    public void onError(ProducerRecord producerRecord, Exception exception) {
        log.info("message send error:{}", producerRecord.toString());
    }
}
