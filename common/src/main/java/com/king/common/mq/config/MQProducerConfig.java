package com.king.common.mq.config;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.TransactionCheckListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author sinjinsong
 * @date 2017/12/26
 */
//@Configuration
public class MQProducerConfig {
    @Value("${spring.rocketmq.producer.group-name}")
    private String groupName;
    @Value("${spring.rocketmq.producer.namesrv-addr}")
    private String namesrvAddr;
    @Value("${spring.rocketmq.producer.topic}")
    private String topic;
    @Value("${spring.rocketmq.producer.confirm-message-faiure-retry-times}")  
    private Integer retryTimes;
    public static final Integer CHECK_GAP = 1; 
    @Value("${spring.rocketmq.producer.check-keys}")
    private String checkKeys;
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Bean
    public MQProducer mqProducer() throws MQClientException {
        TransactionMQProducer producer = new TransactionMQProducer(groupName);
        producer.setNamesrvAddr(namesrvAddr); 
        producer.setTransactionCheckListener(new TransactionCheckListener() {
            @Override
            public LocalTransactionState checkLocalTransactionState(MessageExt msg) {
                // doNothing
                return LocalTransactionState.COMMIT_MESSAGE;
            }
        });
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				producer.shutdown();
			}
		}));
        producer.start();
        logger.info("producer started!");
        return producer;
    }
}
