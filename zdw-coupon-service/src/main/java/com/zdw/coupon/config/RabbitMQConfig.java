package com.zdw.coupon.config;



import lombok.Data;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;


import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: 曾德威
 * @Date: 2023/5/5
 * @Description: 欢迎访问我的个人博客:javazdw.top
 */

@Configuration
@Data
public class RabbitMQConfig {


    /**
     * 交换机
     */
    @Value("${mqconfig.coupon_event_exchange}")
    private String eventExchange;


    /**
     * 第一个队列  延迟队列，
     */
    @Value("${mqconfig.coupon_release_delay_queue}")
    private String couponReleaseDelayQueue;

    /**
     * 第一个队列的路由key
     * 进入队列的路由key
     */
    @Value("${mqconfig.coupon_release_delay_routing_key}")
    private String couponReleaseDelayRoutingKey;


    /**
     * 第二个队列，被监听恢复库存的队列
     */
    @Value("${mqconfig.coupon_release_queue}")
    private String couponReleaseQueue;

    /**
     * 第二个队列的路由key
     *
     * 即进入死信队列的路由key
     */
    @Value("${mqconfig.coupon_release_routing_key}")
    private String couponReleaseRoutingKey;

    /**
     * 过期时间
     */
    @Value("${mqconfig.ttl}")
    private Integer ttl;

    /**1：使用默认的连接工厂，只需要在配置文件中配置全局参数即可
     * 2：如果你的项目需要连接多个MQ，或者要自定义连接配置，不想用全局的配置，那么就需要自己去设置连接工厂了
     * @Bean("myConnectionFactory")
     *     public CachingConnectionFactory connectionFactory(){
     *         CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
     *         connectionFactory.setUsername("guest");
     *         connectionFactory.setPassword("guest");
     *         connectionFactory.setHost("127.0.0.1");
     *         connectionFactory.setPort(5672);
     *         return connectionFactory;
     *     }
            自定义连接工厂
    新版：setPublisherConfirmType :
    NONE值是禁用发布确认模式，是默认值
    CORRELATED值是发布消息成功到交换器后会触发回调方法，如1示例
    SIMPLE值经测试有两种效果，其一效果和CORRELATED值一样会触发回调方法，
    其二在发布消息成功后使用rabbitTemplate调用waitForConfirms或waitForConfirmsOrDie方法等待broker节点返回发送结果，根据返回结果来判定下一步的逻辑，要注意的点是waitForConfirmsOrDie方法如果返回false则会关闭channel，则接下来无法发送消息到broker;

     * @return
     */
//    @Bean
//    public ConnectionFactory connectionFactory(){
//        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
//        connectionFactory.setAddresses("110.40.169.113"+":"+"5672");
//        connectionFactory.setUsername("admin");
//        connectionFactory.setPassword("zdw961898");
//        connectionFactory.setVirtualHost("/");
//        // 如果要进行消息的回调，这里必须要设置为true
//        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.SIMPLE);
//        return connectionFactory;
//
//    }
    //TODO 使用Template，给生产者、消费者  方便发消息 5672
//    @Bean
//    public RabbitTemplate newRabbitTemplate() {
//        RabbitTemplate template = new RabbitTemplate(connectionFactory());
//        //进行 发送确认的回调方法的设置
//        template.setConfirmCallback(confirmCallback()); // 创建方法
//        // 开启路由失败通知
//        template.setMandatory(true);
//        // 路由失败的回调----这里只关注路由失败的
//        template.setReturnCallback(returnCallback()) ;// 创建方法
//        return template;
//    }
//
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 创建交换机 Topic类型，也可以用dirct路由
     * 一般一个微服务一个交换机
     * @return
     */
    @Bean
    public Exchange couponEventExchange(){
        return new TopicExchange(eventExchange,true,false);
    }


    /**
     * 延迟队列
     */
    @Bean
    public Queue couponReleaseDelayQueue(){

        Map<String,Object> args = new HashMap<>(3);
        args.put("x-message-ttl",ttl);
        args.put("x-dead-letter-routing-key",couponReleaseRoutingKey);
        args.put("x-dead-letter-exchange",eventExchange);

        return new Queue(couponReleaseDelayQueue,true,false,false,args);
    }


    /**
     * 普通队列-死信队列，用于被监听
     */
    @Bean
    public Queue couponReleaseQueue(){

        return new Queue(couponReleaseQueue,true,false,false);

    }


    /**
     * 第一个队列===即 延迟队列与交换机定关系建立
     * @return
     */
    @Bean
    public Binding couponReleaseDelayBinding(){

        return new Binding(couponReleaseDelayQueue,Binding.DestinationType.QUEUE,eventExchange,couponReleaseDelayRoutingKey,null);
    }

    /**
     * 死信队列与交换机绑定关系建立
     * @return
     */
    @Bean
    public Binding couponReleaseBinding(){

        return new Binding(couponReleaseQueue,Binding.DestinationType.QUEUE,eventExchange,couponReleaseRoutingKey,null);
    }


}
