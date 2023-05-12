package com.zdw.order.mq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.zdw.model.OrderMessage;
import com.zdw.order.service.ProductOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * @Author: 曾德威
 * @Date: 2023/5/6
 * @Description: 欢迎访问我的个人博客:javazdw.top
 */

@Slf4j
@Component
@RabbitListener(queues = "${mqconfig.order_close_queue}")
public class ProductOrderMQListener {

    @Autowired
    private ProductOrderService productOrderService;

    /**
     *
     * 消费重复消息，幂等性保证
     * 并发情况下如何保证安全
     *
     * @param orderMessage
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitHandler
    public void closePeoductOrder(OrderMessage orderMessage, Message message, Channel channel) throws IOException {
        log.info("监听到消息:closePeoductOrder:{}",orderMessage);
        long msgTag = message.getMessageProperties().getDeliveryTag();
        try {
           boolean flag =  productOrderService.closePeoductOrder(orderMessage);
           if (flag){
               //重新入队-一个一个确认
               channel.basicAck(msgTag,false);
           }else {
               //重新入队-true
               channel.basicReject(msgTag,true);
           }
        }catch (IOException e){
            //重新入队-true
            log.error("定时关单失败:",orderMessage);
            channel.basicReject(msgTag,true);
        }


    }
}
