package com.sp.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sp.config.exception.GlobalException;
import com.sp.service.ConsumerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by admin on 2019/12/5.
 */
@Component
@Slf4j
public class ConsumerServiceImpl implements ConsumerService {
    @Override
    public ConsumeConcurrentlyStatus consume(List<MessageExt> messages) {
        // 获取消息并处理
        for (MessageExt msg : messages) {
            JSONObject jsonInfo = JSON.parseObject(new String(msg.getBody()));
        }

        try {
            doBusiness();
        } catch (Exception e) {
            log.error("xxx异常", e);
        }

        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    private void doBusiness() {
    }


}
