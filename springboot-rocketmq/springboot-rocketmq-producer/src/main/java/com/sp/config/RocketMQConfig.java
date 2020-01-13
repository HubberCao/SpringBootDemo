package com.sp.config;

import com.sp.common.contants.Constants;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * Created by admin on 2019/12/4.
 */
@Configuration
public class RocketMQConfig {

    @Bean
    public TransactionMQProducer transactionMQProducer() {
        TransactionMQProducer producer = new TransactionMQProducer();
        producer.setNamesrvAddr(Constants.ROCKETMQ_NAMESRV_ARRR);
        ExecutorService executorService = new ThreadPoolExecutor(3, 5, 100, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(200), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("RocketMQ transaction thread");
                return t;
            }
        });
        // 开启多线程，用于回查
        producer.setExecutorService(executorService);

        // 以下在业务应用中设置
        /**
         * //设置回调事务检查监听器
         producer.setTransactionListener(transactionListener);
         try {
         producer.start();
         } catch (MQClientException e) {
         throw new RuntimeException("produce回调时发生异常");
         }
         */

        return producer;
    }
}
