//package com.demo.config;
//
//import com.rabbitmq.client.Channel;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.core.Message;
//import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
//import org.springframework.stereotype.Component;
//
//import java.io.ByteArrayInputStream;
//import java.io.ObjectInputStream;
//import java.util.Map;
//
///**
// * <p> @Title MyAckReceiver
// * <p> @Description 手动确认消息监听类
// *
// * @author ACGkaka
// * @date 2023/1/12 19:05
// */
//@Slf4j
//@Component
//public class MyAckReceiver implements ChannelAwareMessageListener {
//
//    @Override
//    public void onMessage(Message message, Channel channel) throws Exception {
//        long deliveryTag = message.getMessageProperties().getDeliveryTag();
//        try {
//            byte[] body = message.getBody();
//            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(body));
//            Map<String, String> msgMap = (Map<String, String>) ois.readObject();
//            ois.close();
//            System.out.println("MyAckReceiver :" + msgMap);
//            System.out.println("消费的主题消息来自：" + message.getMessageProperties().getConsumerQueue());
//            // 第二个参数，手动确认可以被批处理，当该参数为 true 时，则可以一次性确认 delivery_tag 小于等于传入值的所有消息
//            channel.basicAck(deliveryTag, true);
//            /// 第二个参数，true 会重新放回队列，所以需要自己根据业务逻辑判断什么时候使用拒绝
////            channel.basicReject(deliveryTag, true);
//        } catch (Exception e) {
//            channel.basicReject(deliveryTag, false);
//            log.error(e.getMessage(), e);
//        }
//    }
//}
