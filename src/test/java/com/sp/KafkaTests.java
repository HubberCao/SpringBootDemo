package com.sp;

import com.sp.common.contants.Constants;
import com.sp.config.kafka.KafkaSendResultHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by admin on 2019/11/23.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class KafkaTests {

    @Autowired
    private AdminClient adminClient;

    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;

    @Autowired
    private ReplyingKafkaTemplate replyingKafkaTemplate;

    @Autowired
    private KafkaSendResultHandler kafkaSendResultHandler;

    /**
     * 手动编码创建topic
     * @throws InterruptedException
     */
    @Test
    public void testCreateTopic() throws InterruptedException {
        NewTopic newTopic = new NewTopic(Constants.TOPIC_TWO, 3, (short)1);
        adminClient.createTopics(Arrays.asList(newTopic));
        TimeUnit.SECONDS.sleep(1);
    }

    /**
     * 查询Topic信息
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void testSelectTopicInfo() throws ExecutionException, InterruptedException {
        DescribeTopicsResult result = adminClient.describeTopics(Arrays.asList(Constants.TOPIC_TWO));
        result.all().get().forEach((k,v)->log.info("k: "+k+" ,v: "+v.toString()+"\n"));
    }

    @Test
    public void testProducerListen() throws InterruptedException {
        kafkaTemplate.setProducerListener(kafkaSendResultHandler);
        kafkaTemplate.send(Constants.TOPIC_DEFAULT, "测试生产者监听");
        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    public void testAck() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            kafkaTemplate.send(Constants.TOPIC_ACK, i+"");
        }
    }

    @Test
    public void testSyncSend() throws ExecutionException, InterruptedException, TimeoutException {
        kafkaTemplate.send(Constants.TOPIC_ONE, "发送同步消息");
        //kafkaTemplate.send(Constants.TOPIC_DEMO, "发送同步消息").get(1, TimeUnit.MICROSECONDS);
    }

    @Test
    public void testTemplateSend() {
        //发送带有时间戳的消息
        kafkaTemplate.send(Constants.TOPIC_DEFAULT, 0, System.currentTimeMillis(), 0, "send message with timestamp");

        //使用ProducerRecord发送消息
        ProducerRecord record = new ProducerRecord(Constants.TOPIC_DEFAULT, "use ProducerRecord to send message");
        kafkaTemplate.send(record);

        //使用Message发送消息
        Map map = new HashMap();
        map.put(KafkaHeaders.TOPIC, Constants.TOPIC_DEFAULT);
        map.put(KafkaHeaders.PARTITION_ID, 0);
        map.put(KafkaHeaders.MESSAGE_KEY, 0);
        GenericMessage message = new GenericMessage("use Message to send message",new MessageHeaders(map));
        kafkaTemplate.send(message);
    }

    /**
     * 事务-@Transactional注解方式
     */
    @Test
    @Transactional
    public void testTransactionalAnnotation() {
        kafkaTemplate.send(Constants.TOPIC_TRANS, "测试事务注解222");
        throw new RuntimeException("fail");
    }

    /**
     * KafkaTemplate.executeInTransaction开启事务, 局部事务
     */
    @Test
    public void testExecuteInTransaction() {
        kafkaTemplate.executeInTransaction(new KafkaOperations.OperationsCallback() {
            @Override
            public Object doInOperations(KafkaOperations operations) {
                operations.send(Constants.TOPIC_TRANS,"测试 executeInTransaction");
                throw new RuntimeException("fail2");
               // return null;
            }
        });
    }

    @Test
    public void testBatch() {
        for (int i = 0; i < 12; i++) {
            kafkaTemplate.send(Constants.TOPIC_BATCH, "test batch listener,dataNum-" + i);
        }
    }

    @Test
    public void testForward() {
        kafkaTemplate.send(Constants.TOPIC_TARGET, "test @SendTo");
    }


    @Test
    public void testReplyingKafkaTemplate() throws ExecutionException, InterruptedException, TimeoutException {
        ProducerRecord<String, String> record = new ProducerRecord<>(Constants.TOPIC_REQUEST, "this is a message");
        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, Constants.TOPIC_REPLY.getBytes()));
        RequestReplyFuture<String, String, String> replyFuture = replyingKafkaTemplate.sendAndReceive(record);
        // 获取发送结果
        SendResult<String, String> sendResult = replyFuture.getSendFuture().get();
        log.info("Sent ok: {}", sendResult.getRecordMetadata());
        // 获取响应结果
        ConsumerRecord<String, String> consumerRecord = replyFuture.get();
        log.info("Return value: {}", consumerRecord.value());
        Thread.sleep(20000);
    }

    @Test
    public void testTask() {
        for (int i = 0; i < 10; i++) {
            kafkaTemplate.send("topic.durable", "this is durable message");
        }
    }

    @Test
    public void testFilter() throws InterruptedException {
        String data = String.valueOf(System.currentTimeMillis());
        log.info("发送消息, data: {}", data);
        kafkaTemplate.send(Constants.TOPIC_FILTER, data);
    }

    @Test
    public void testErrorHandler() {
        kafkaTemplate.send(Constants.TOPIC_ERROR, "test error handle");
    }

    @Test
    public void testBatchErrorHandler() {
        List<String> errList = new ArrayList<>();
        errList.add("err1");
        errList.add("err2");
        //kafkaTemplate.send(Constants.TOPIC_ERROR, errList);
    }
}
