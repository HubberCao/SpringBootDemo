package com.sp.service.kafka;

import com.sp.common.contants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;

/**
 * Created by admin on 2019/11/22.
 */
//@Component
@Slf4j
public class KafkaConsumer {


    @KafkaListener(topics = Constants.TOPIC_TRANS)
    public void receive(@Header(KafkaHeaders.RECEIVED_TOPIC) String topic, String message) {
        log.info("消费topic:{}, message:{}", topic, message);
    }

    @KafkaListener(topics = Constants.TOPIC_REAL)
    public void receive2(@Header(KafkaHeaders.RECEIVED_TOPIC) String topic, String message) {
        log.info("2消费topic:{}, message:{}", topic, message);
    }

}
