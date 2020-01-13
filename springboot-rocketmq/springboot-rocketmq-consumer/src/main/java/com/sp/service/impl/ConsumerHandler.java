package com.sp.service.impl;

import com.sp.service.ConsumerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by admin on 2019/12/5.
 */
@Component
@Slf4j
public class ConsumerHandler implements CommandLineRunner {
    @Autowired
    private ConsumerService consumerService;

    @Override
    public void run(String... strings) throws Exception {
        userListener();
    }

    private void userListener() throws MQClientException {
        // 实例化组名称，提供消费者对象
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("userDemo");
        consumer.setNamesrvAddr("192.168.1.120:9876");
        // 为防止指定的消费组是一个新的消费组，所以指定从何处开始
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        // 订阅一个要使用的主题
        consumer.subscribe("userTopic", "*");
        // 注册回调函数，以便在从代理获取的消息到达时执行
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messages, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                ConsumeConcurrentlyStatus status = consumerService.consume(messages);
                log.info(status.toString());

                return status;
            }
        });

        // 启动使用者实例
        consumer.start();
        log.info("消费端开始消费消息");
    }
}
