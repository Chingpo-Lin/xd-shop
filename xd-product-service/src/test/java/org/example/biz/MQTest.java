package org.example.biz;

import lombok.extern.slf4j.Slf4j;
import org.example.ProductApplication;
import org.example.model.ProductMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProductApplication.class)
@Slf4j
public class MQTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void sendDelayMsg() {
        rabbitTemplate.convertAndSend("stock.event.exchange",
                "stock.release.delay.routing.key",
                "this is product stock lock msg");
    }

    @Test
    public void testSendProductStockMessage() {
        ProductMessage productMessage = new ProductMessage();
        productMessage.setOutTradeNo("123456abc");
        productMessage.setTaskId(1L);
        rabbitTemplate.convertAndSend("stock.event.exchange",
                "stock.release.delay.routing.key", productMessage);
    }
//    @Test
//    public void testRecordRelease() {
//        CouponRecordMessage message = new CouponRecordMessage();
//        message.setOutTradeNo("123456abc");
//        message.setTaskId(1L);
//        rabbitTemplate.convertAndSend("coupon.event.exchange",
//                "coupon.release.delay.routing.key", message);
//    }
}
