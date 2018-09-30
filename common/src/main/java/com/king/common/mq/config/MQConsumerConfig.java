package com.king.common.mq.config;

import javax.annotation.PostConstruct;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


/**
 * 
 * @author King chen
 * @emai 396885563@qq.com
 * @data 2018年9月19日
 */
//@Configuration
public class MQConsumerConfig {
    private DefaultMQPushConsumer consumer;
    
    @Value("${spring.rocketmq.consumer.group-name}")
    private String groupName;
    @Value("${spring.rocketmq.cosumer.namesrv-addr}")
    private String namesrvAddr;
    @Value("${spring.rocketmq.consumer.topic}")
    private String topic;
    private Logger logger = LoggerFactory.getLogger(getClass());
    
   /* @Autowired
    private TransactionCheckMessageListener transactionCheckMessageListener;
    @Value("${spring.rocketmq.consumer.consume-failure-retry-times}")
    private Integer retryTimes;
    
    @PostConstruct
    public void init() throws MQClientException {
        this.consumer = new DefaultMQPushConsumer(groupName);
        this.consumer.setNamesrvAddr(namesrvAddr);
        // 启动后从队列头部开始消费
        this.consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        this.consumer.subscribe(topic, "*");
        this.consumer.registerMessageListener(transactionCheckMessageListener);
        this.consumer.start();
        logger.info("consumer started!");
    }*/
}
