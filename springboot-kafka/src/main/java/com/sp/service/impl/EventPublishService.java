package com.sp.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sp.bean.model.EventPublish;
import com.sp.common.enums.EventPublishStatus;
import com.sp.dao.EventPublishDao;
import com.sp.service.kafka.KafkaUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2019/12/2.
 */
@Service
@Slf4j
public class EventPublishService {
    @Autowired
    private EventPublishDao eventPublishDao;

    @Transactional(rollbackFor = Exception.class)
    public void publish() {
        //查询所有状态为NEW的事件
        Map<String, Object> params = Maps.newHashMap();
        params.put("status", EventPublishStatus.NEW.name());
        List<EventPublish> eventPublishes = eventPublishDao.list(params);
        if (!CollectionUtils.isEmpty(eventPublishes)) {
            //发送消息队列
            List<Long> ids = sendEventPublish(eventPublishes);
            if (!CollectionUtils.isEmpty(ids)) {
                //更新状态为PUBLISHED
                //eventPublishDao.updateStatus(ids, EventPublishStatus.PUBLISHED.name());
            }
        }
    }

    /**
     * 发送EventPublish到消息队列
     *
     * @param eventPublishes EventPublish对象集合
     * @return 发送成功的EventPublish的ID集合
     */
    private static List<Long> sendEventPublish(List<EventPublish> eventPublishes) {
        if (CollectionUtils.isEmpty(eventPublishes)) {
            return Collections.emptyList();
        }
        List<Long> ids = Lists.newArrayList();
        for (EventPublish eventPublish : eventPublishes) {
            try {
                KafkaUtils.sendSync(eventPublish.getEventType().name(), eventPublish.getPayload());
                ids.add(eventPublish.getId());
            } catch (Exception e) {
                log.error("发送Kafka消息失败，eventPublish={}", eventPublish, e);
            }
        }
        log.debug("发送Kafka消息成功，ids={}", ids);
        return ids;
    }
}
