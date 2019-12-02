package com.sp.service.kafka;

import com.sp.common.contants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by admin on 2019/11/22.
 */
@Component
//@EnableScheduling
@Slf4j
public class KafkaProducer {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    //@Scheduled(cron = "00/30 * * * * ?")
    public void send() {
        String msg = "data, time=" + System.currentTimeMillis();

        log.info("开始发送消息, msg={}", msg);
        kafkaTemplate.send(Constants.TOPIC_ONE, msg);
    }
}
