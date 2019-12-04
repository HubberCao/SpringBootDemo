package com.sp.service.impl;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.sp.service.kafka.KafkaUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by admin on 2019/12/2.
 */
//@Service
public class EventProcessService {
    @Autowired
    private EventProcessDao eventProcessDao;

    @PostConstruct
    public void init() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("MqMessageConsumeThread-%d")
                .setDaemon(true)
                .build();
        ExecutorService executorService = Executors.newSingleThreadExecutor(threadFactory);
        executorService.execute(new MqMessageConsumeThread());
    }

    private class MqMessageConsumeThread implements Runnable {
        @Override
        public void run() {
            KafkaUtils.consume(consumerRecord -> {
                EventProcess eventProcess = new EventProcess();
                eventProcess.setPayload(consumerRecord.value());
                eventProcess.setEventType(EventType.USER_CREATED);
                eventProcess.setStatus(EventProcessStatus.NEW);
                eventProcessDao.save(eventProcess);
            });
        }
    }
}
