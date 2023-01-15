//package com.demo.config;
//
//import org.springframework.amqp.core.AcknowledgeMode;
//import org.springframework.amqp.core.Queue;
//import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
//import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * <p> @Title MessageListenerConfig
// * <p> @Description 监听器配置
// *
// * @author ACGkaka
// * @date 2023/1/12 19:47
// */
//@Configuration
//public class MessageListenerConfig {
//
//    @Autowired
//    private CachingConnectionFactory connectionFactory;
//    /** 消息接收处理类 */
//    @Autowired
//    private MyAckReceiver myAckReceiver;
//
//    @Bean
//    public SimpleMessageListenerContainer simpleMessageListenerContainer() {
//        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
//        container.setConcurrentConsumers(1);
//        container.setMaxConcurrentConsumers(1);
//        // RabbitMQ 默认是自动确认，这里改为手动确认消息
//        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
//
//        // 设置一个队列
//        container.setQueueNames(RabbitDirectConfig.DIRECT_QUEUE_NAME);
//        // 如果同时设置多个如下： 前提是队列都是必须已经创建存在的
////        container.setQueueNames(RabbitDirectConfig.DIRECT_QUEUE_NAME, "testDirectQueue2", "testDirectQueue3");
//
//        // 另一种设置队列的方法，如果使用这种情况，那么要设置多个，就使用addQueues
////        container.setQueues(new Queue(RabbitDirectConfig.DIRECT_QUEUE_NAME));
////        container.addQueues(new Queue("testDirectQueue2"));
////        container.addQueues(new Queue("testDirectQueue3"));
//        container.setMessageListener(myAckReceiver);
//
//        return container;
//    }
//}
