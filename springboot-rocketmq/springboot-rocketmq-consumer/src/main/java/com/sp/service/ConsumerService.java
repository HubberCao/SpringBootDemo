package com.sp.service;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * Created by admin on 2019/12/5.
 */
public interface ConsumerService {

    ConsumeConcurrentlyStatus consume(List<MessageExt> messages);
}
