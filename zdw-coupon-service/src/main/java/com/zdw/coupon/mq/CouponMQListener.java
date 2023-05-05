package com.zdw.coupon.mq;

import com.rabbitmq.client.Channel;
import com.zdw.coupon.service.CouponRecordService;
import com.zdw.model.CouponRecordMessage;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;



@Slf4j
@Component
@RabbitListener(queues = "${mqconfig.coupon_release_queue}")
public class CouponMQListener {


    @Autowired
    private CouponRecordService couponRecordService;

    @Autowired
    private RedissonClient redissonClient;

    /**
     *
     * 重复消费-幂等性
     *
     * 消费失败，重新入队后最大重试次数：
     *  如果消费失败，不重新入队，可以记录日志，然后插到数据库人工排查
     *
     *  消费者这块还有啥问题，大家可以先想下，然后给出解决方案
     *
     * @param recordMessage
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitHandler
    public void releaseCouponRecord(CouponRecordMessage recordMessage, Message message, Channel channel) throws IOException {

       log.info("监听到信息：releaseCouponRecord消息内容：{}",recordMessage);

       long msgTag = message.getMessageProperties().getDeliveryTag();
        // 消费成功 释放我们锁定优惠卷记录
        boolean flag = couponRecordService.releaseCouponRecord(recordMessage);
        try {
            if (flag){
                channel.basicAck(msgTag,false);
            }else{
                log.error("释放优惠卷 flag= false,{}",recordMessage);
                channel.basicReject(msgTag,true);
            }
        }catch (IOException e){
            e.printStackTrace();
            log.error("释放优惠卷记录异常：{},msg：{}",e,recordMessage);
            channel.basicReject(msgTag,true);
        }

    }


//    @RabbitHandler
//    public void releaseCouponRecord2(String msg,Message message, Channel channel) throws IOException {
//
//        log.info(msg);
//        channel.basicAck(message.getMessageProperties().getDeliveryTag(),true);
//    }

}
