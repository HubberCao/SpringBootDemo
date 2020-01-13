package com.sp.service.impl;

import com.alibaba.fastjson.JSON;
import com.sp.bean.model.User;
import com.sp.config.exception.GlobalException;
import com.sp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by admin on 2019/12/4.
 */
@Component
@Slf4j
public class UserTransactionListener implements TransactionListener {
    @Autowired
    private TransactionMQProducer producer;

    @Autowired
    private UserService userService;

    @PostConstruct
    public void init() {
        producer.setProducerGroup("userDemo");
        // 设置回调事务检查监听器
        producer.setTransactionListener(this);
        try {
            producer.start();
        } catch (MQClientException e) {
            throw new GlobalException("produce回调时发生异常", e);
        }

    }

    /**
     * step 1 发送prepare消息
     * @param bizCode
     * @param bizObj
     */
    public void sendPrepareMsg(String bizCode, Object bizObj) {
        try {
            Message msg = new Message("userTopic", "tag",
                    bizCode, JSON.toJSONString(bizObj).getBytes(RemotingHelper.DEFAULT_CHARSET));
            //生产者通过sendMessageInTransaction发送事务消息
            SendResult sendResult = producer.sendMessageInTransaction(msg, null);
            log.info("bizCode={},prepare事务消息发送:{}", bizCode, sendResult.getSendStatus());
        } catch (Exception e) {
            throw new GlobalException("prepare事务消息发送异常, bizCode=" + bizCode);
        }
    }

    /**
     * step 2 这个方法会在每一条消息发出去后执行，保证事务的一致。
     * @param msg
     * @param o
     * @return
     */
    @Override
    public LocalTransactionState executeLocalTransaction(Message msg, Object o) {
        User user = JSON.parseObject(msg.getBody(), User.class);
        LocalTransactionState state = LocalTransactionState.UNKNOW;
        try {
            boolean result = false; // 业务方法调用 doBusiness();
            if (result) {
                state = LocalTransactionState.COMMIT_MESSAGE;
            } else {
                state = LocalTransactionState.ROLLBACK_MESSAGE;
            }
        } catch (GlobalException e) {
            log.error("处理本地事务异常", e);
            state = LocalTransactionState.ROLLBACK_MESSAGE;
        }

        return state;
    }

    /**
     * step 3 每隔一段时间 rocketMQ 会回调这个方法， 判断每一条消息是否提交。防止 消息状态停滞 或者出现超时的情况
     * @param msg
     * @return
     */
    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {
        LocalTransactionState state = LocalTransactionState.UNKNOW;
        try {
            boolean checkResult = false; // msg.getTransactionId()
            if (checkResult) {
                state = LocalTransactionState.COMMIT_MESSAGE;
            } else {
                state = LocalTransactionState.ROLLBACK_MESSAGE;
            }
        } catch (GlobalException e) {
            log.error("检查本地事务异常", e);
            state = LocalTransactionState.ROLLBACK_MESSAGE;
        }

        return state;
    }
}
