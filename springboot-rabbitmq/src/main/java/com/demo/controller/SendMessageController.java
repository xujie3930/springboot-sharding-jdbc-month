package com.demo.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * <p> @Title SendMessageController
 * <p> @Description 推送消息接口
 *
 * @author ACGkaka
 * @date 2023/1/12 15:23
 */
@RestController
public class SendMessageController {

    /**
     * 使用 RabbitTemplate，这提供了接收/发送等方法。
     */
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/sendFanoutMessage")
    public String sendFanoutMessage() {
        String messageId = String.valueOf(UUID.randomUUID());
        String messageData = "Hello world.";
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String, Object> map = new HashMap<>();
        map.put("messageId", messageId);
        map.put("messageData", messageData);
        map.put("createTime", createTime);
        // 将消息携带绑定键值：testFanoutRouting，发送到交换机：testFanoutExchange
        rabbitTemplate.convertAndSend("testFanoutExchange", "testFanoutRouting", map);
        return "OK";
    }

}
