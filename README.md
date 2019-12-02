
## kafka

### 配置方式
    1. properties/yaml 文件的方式
    2. KafkaConfig, java配置的方式
    
### Topic
    1. 创建topic方式:
    (1). @Bean
    (2). 手动编码创建Topic
    
    2. topic 的维护
    修改、查看、删除
    
### 消息
    发送消息
        1. 异步
        2. 同步，send方法后面调用get方法
    
        注意：
            1. 发送消息的时候需要休眠一下，否则发送时间较长的时候会导致进程提前关闭导致无法调用回调时间。主要是因为KafkaTemplate发送消息是采取异步方式发送的
    消息结果回调
        1. 实现接口 ProducerListener
        2. 调用时设置producerListener， kafkaTemplate.setProducerListener(producerListener);
    
### Kafka 事务
    开启事务
        1. 配置KafkaTransactionManager
        2. 在producerFactory中开启事务功能，并设置TransactionIdPrefix
    
    Kafka使用事务的两种方式
        1. 配置Kafka事务管理器并使用@Transactional注解
        2. 使用KafkaTemplate的executeInTransaction方法    

### 消息监听
    1. 单条、批量消费
    2. Ack机制确认消费
        (1)设置ENABLE_AUTO_COMMIT_CONFIG=false，禁止自动提交
        (2)设置AckMode=MANUAL_IMMEDIATE
        (3)监听方法加入Acknowledgment ack 参数
        
        重复消费未被Ack的消息，解决办法有如下
            (1)重新将消息发送到队列中，这种方式比较简单而且可以使用Headers实现第几次消费的功能
            (2)使用Consumer.seek方法，重新回到该未ack消息偏移量的位置重新消费，这种可能会导致死循环，原因出现于业务一直没办法处理这条数据，但还是不停的重新定位到该数据的偏移量上
    3. 监听Topic中指定的分区，使用@TopicPartition
    4. 注解方式获取消息头及消息体
    5. 定时启动（禁止自启动）
        (1)禁止KafkaListener自启动（AutoStartup）， 设置factory.setAutoStartup(false);
        (2)通过定时任务启禁用
    6. 消息过滤器
        为监听容器工厂配置一个RecordFilterStrategy(消息过滤策略)，返回true的时候消息将会被抛弃，返回false时，消息能正常抵达监听容器
    7. 异常处理器
        通过ConsumerAwareErrorHandler 单条/批量处理异常    
### 消息转发
    1. 使用Headers设置回复主题（Reply_Topic），这种方式比较特别，是一种请求响应模式，使用的是ReplyingKafkaTemplate类，响应时间可能会较慢
        测试时可能出现问题：perhaps timed out, or using a shared reply topic
        解决： 设置参数，template.setSharedReplyTopic(true);
    2. 手动转发，使用@SendTo注解将监听方法返回值转发到Topic中
### 注意事项
    
### 工具
    Kafka Tool 2
    一款Kafka的可视化客户端工具，可以非常方便的查看Topic的队列信息以及消费者信息以及kafka节点信息。
    下载地址：http://www.kafkatool.com/download.html
    
参考
- [Spring-Kafka史上最强入门教程](https://www.jianshu.com/c/0c9d83802b0c?order_by=commented_at)