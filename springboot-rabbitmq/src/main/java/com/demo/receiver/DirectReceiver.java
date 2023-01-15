package com.demo.receiver;

import com.demo.config.RabbitDirectConfig;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * <p> @Title DirectReceiver
 * <p> @Description 直连交换机监听类
 *
 * @author ACGkaka
 * @date 2023/1/12 15:59
 */
@Component
@RabbitListener(queues = RabbitDirectConfig.DIRECT_QUEUE_NAME)
public class DirectReceiver {

    @RabbitHandler
    public void process(Map testMessage) {
        System.out.println("DirectReceiver消费者收到消息：" + testMessage.toString());
    }
}
