package com.demo.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p> @Title RabbitDirectConfig
 * <p> @Description 直连交换机配置
 * Direct Exchange是RabbitMQ默认的交换机模式，也是最简单的模式，根据key全文匹配去寻找队列。
 *
 * @author ACGkaka
 * @date 2023/1/12 15:09
 */
@Configuration
public class RabbitDirectConfig {

    public static final String DIRECT_QUEUE_NAME = "testDirectQueue";
    public static final String DIRECT_EXCHANGE_NAME = "testDirectExchange";
    public static final String DIRECT_ROUTING_NAME = "testDirectRouting";

    /**
     * 队列，命名：testDirectQueue
     *
     * @return 队列
     */
    @Bean
    public Queue testDirectQueue() {
        // durable:是否持久化，默认是false，持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
        // exclusive:默认false，只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参考优先级高于durable。
        // autoDelete:是否自动删除，当没有生产者或消费者使用此队列，该队列会自动删除。

        // 一般设置一下队列的持久化就好，其余两个默认false
        return new Queue(RabbitDirectConfig.DIRECT_QUEUE_NAME, true);
    }

    /**
     * Direct交换机，命名：testDirectExchange
     * @return Direct交换机
     */
    @Bean
    DirectExchange testDirectExchange() {
        return new DirectExchange(RabbitDirectConfig.DIRECT_EXCHANGE_NAME, true, false);
    }

    /**
     * 绑定 将队列和交换机绑定，并设置用于匹配键：testDirectRouting
     * @return 绑定
     */
    @Bean
    Binding bindingDirect() {
        return BindingBuilder.bind(testDirectQueue()).to(testDirectExchange()).with(RabbitDirectConfig.DIRECT_ROUTING_NAME);
    }
}
