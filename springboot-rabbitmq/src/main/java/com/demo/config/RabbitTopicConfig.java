package com.demo.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p> @Title RabbitTopicConfig
 * <p> @Description 主题交换机配置类
 *
 * @author zhj
 * @date 2023/1/16 6:02
 */
@Configuration
public class RabbitTopicConfig {

    public static final String TOPIC_QUEUE_NAME_1 = "testTopicQueue1";
    public static final String TOPIC_QUEUE_NAME_2 = "testTopicQueue2";
    public static final String TOPIC_QUEUE_NAME_3 = "testTopicQueue3";
    public static final String TOPIC_EXCHANGE_NAME = "testTopicExchange";
    public static final String TOPIC_ROUTING_NAME_1 = "test";
    public static final String TOPIC_ROUTING_NAME_2 = "test.topic";
    public static final String TOPIC_ROUTING_NAME_3 = "test.topic.message";

    @Bean
    public Queue testTopicQueue1() {
        return new Queue(RabbitTopicConfig.TOPIC_QUEUE_NAME_1);
    }
    @Bean
    public Queue testTopicQueue2() {
        return new Queue(RabbitTopicConfig.TOPIC_QUEUE_NAME_2);
    }
    @Bean
    public Queue testTopicQueue3() {
        return new Queue(RabbitTopicConfig.TOPIC_QUEUE_NAME_3);
    }
    /**
     * 交换机(Exchange) 描述：接收消息并且转发到绑定的队列，交换机不存储消息
     */
    @Bean
    TopicExchange testTopicExchange() {
        return new TopicExchange(RabbitTopicConfig.TOPIC_EXCHANGE_NAME, true, false);
    }

    /**
     * 綁定队列 testTopicQueue1() 到 testTopicExchange 交换机,路由键只接受完全匹配 test.topic1 的队列接受者可以收到消息
     */
    @Bean
    Binding bindingTestTopic1(Queue testTopicQueue1, TopicExchange testTopicExchange) {
        return BindingBuilder.bind(testTopicQueue1).to(testTopicExchange).with(RabbitTopicConfig.TOPIC_ROUTING_NAME_1);
    }
    @Bean
    Binding bindingTestTopic2(Queue testTopicQueue2, TopicExchange testTopicExchange) {
        return BindingBuilder.bind(testTopicQueue2).to(testTopicExchange).with(RabbitTopicConfig.TOPIC_ROUTING_NAME_2);
    }
    @Bean
    Binding bindingTestTopic3(Queue testTopicQueue3, TopicExchange testTopicExchange) {
        return BindingBuilder.bind(testTopicQueue3).to(testTopicExchange).with(RabbitTopicConfig.TOPIC_ROUTING_NAME_3);
    }
}
