package com.sp.config.kafka;

import com.sp.common.contants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;

/**
 * 消息过滤器
 * Created by admin on 2019/11/24.
 */
//@Component
@Slf4j
public class FilterListener {
    @Autowired
    private ConsumerFactory consumerFactory;

    @Bean
    public ConcurrentKafkaListenerContainerFactory filterContainerFactory() {
        ConcurrentKafkaListenerContainerFactory factory = new ConcurrentKafkaListenerContainerFactory();
        factory.setConsumerFactory(consumerFactory);
        //配合RecordFilterStrategy使用，被过滤的信息将被丢弃
        factory.setAckDiscarded(true);
        factory.setRecordFilterStrategy(new RecordFilterStrategy() {
            @Override
            public boolean filter(ConsumerRecord consumerRecord) {
                long data = Long.parseLong((String) consumerRecord.value());
                log.info("filterContainerFactory filter : "+data);
                if (data % 2 == 0) {
                    return false;
                }
                //返回true将会被丢弃
                return true;
            }
        });

        return factory;
    }

    @KafkaListener(id = "filterCons", topics = Constants.TOPIC_FILTER, containerFactory = "filterContainerFactory")
    public void filterListener(String data) {
        //这里做数据持久化的操作
        log.error("topic.filter receive : " + data);
    }
}
