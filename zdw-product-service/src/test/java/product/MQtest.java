package product;

import com.zdw.model.CouponRecordMessage;
import com.zdw.product.ProductAppliction;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author: 曾德威
 * @Date: 2023/5/5
 * @Description: 欢迎访问我的个人博客:javazdw.top
 */


@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProductAppliction.class)
@Slf4j
public class MQtest {

@Autowired
private RabbitTemplate rabbitTemplate;
    @Test
    public void sendDelayMsg(){
        rabbitTemplate.convertAndSend("stock.event.exchange","stock.release.delay.routing.key","你好，商品服务的延迟队列");
    }

    // todo 测试
    @Test
    public void testCouponRecordRelease(){

        CouponRecordMessage message = new CouponRecordMessage();
        message.setOutTradeNo("123456abc");
        message.setTaskId(1L);

        rabbitTemplate.convertAndSend("coupon.event.exchange","coupon.release.delay.routing.key",message);



    }



}
