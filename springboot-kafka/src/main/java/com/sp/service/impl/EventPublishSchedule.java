package com.sp.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时器
 * Created by admin on 2019/12/2.
 */
@Component
@Slf4j
public class EventPublishSchedule {
    @Autowired
    private EventPublishService eventPublishService;

    /**
     * 每N毫秒执行一次
     */
    @Scheduled(fixedRate = 5000)
    public void publish() {
        log.debug("EventPublishSchedule execute.");
        eventPublishService.publish();
    }
}
