package com.demo.receiver;

import com.demo.config.RabbitFanoutConfig;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * <p> @Title FanoutReceiver2
 * <p> @Description 广播交换机监听类2
 *
 * @author zhj
 * @date 2023/1/16 5:55
 */
@Component
@RabbitListener(queues = RabbitFanoutConfig.FANOUT_QUEUE_NAME_2)
public class FanoutReceiver2 {

    @RabbitHandler
    public void process(String message) {
        System.out.println("接收者 FanoutReceiver2，消息内容：" + message);
    }
}
