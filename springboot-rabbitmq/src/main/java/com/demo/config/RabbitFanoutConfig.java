package com.demo.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p> @Title RabbitFanoutConfig
 * <p> @Description 广播交换机配置
 * Fanout 就是我们熟悉的广播模式或者订阅模式，给Fanout交换机发送消息，绑定了这个交换机的所有队列都收到这个消息。
 *
 * @author zhj
 * @date 2023/1/16 5:42
 */
@Configuration
public class RabbitFanoutConfig {

    public static final String FANOUT_QUEUE_NAME_1 = "testFanoutQueue1";
    public static final String FANOUT_QUEUE_NAME_2 = "testFanoutQueue2";

    @Bean
    public Queue testFanoutQueue1() {
        return new Queue(RabbitFanoutConfig.FANOUT_QUEUE_NAME_1);
    }
    @Bean
    public Queue testFanoutQueue2() {
        return new Queue(RabbitFanoutConfig.FANOUT_QUEUE_NAME_2);
    }
    /**
     * 任何发送到Fanout Exchange的消息都会被转发到与该Exchange绑定(Binding)的所有队列上。
     */
    @Bean
    FanoutExchange testFanoutExchange() {
        return new FanoutExchange("testFanoutExchange", true, false);
    }
    @Bean
    Binding bindingFanout1(Queue testFanoutQueue1, FanoutExchange testFanoutExchange) {
        return BindingBuilder.bind(testFanoutQueue1).to(testFanoutExchange);
    }
    @Bean
    Binding bindingFanout2(Queue testFanoutQueue2, FanoutExchange testFanoutExchange) {
        return BindingBuilder.bind(testFanoutQueue2).to(testFanoutExchange);
    }
}
