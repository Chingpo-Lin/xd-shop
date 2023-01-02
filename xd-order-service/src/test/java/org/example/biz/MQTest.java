package org.example.biz;

import lombok.extern.slf4j.Slf4j;
import org.example.OrderApplication;
import org.example.model.CouponRecordMessage;
import org.example.model.OrderMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrderApplication.class)
@Slf4j
public class MQTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void sendDelayMsg() {
        rabbitTemplate.convertAndSend("order.event.exchange",
                "order.close.delay.routing.key",
                "this is a new order");
    }

    @Test
    public void testOrderClose() {
        OrderMessage message = new OrderMessage();
        message.setOutTradeNo("123456abc");

        rabbitTemplate.convertAndSend("order.event.exchange",
                "order.close.delay.routing.key", message);
    }
}
