package org.example.mq;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.example.model.CouponRecordMessage;
import org.example.model.ProductMessage;
import org.example.service.ProductService;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.locks.Lock;

@Slf4j
@Component
@RabbitListener(queues = "${mqconfig.stock_release_queue}")
public class ProductStockMQListener {

    @Autowired
    private ProductService productService;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * need to check:
     * redundant consume
     * retry times after consume fail
     * or
     * record log and insert to db after fail instead of requeue
     *
     * @param productMessage
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitHandler
    public void releaseStockRecord(ProductMessage productMessage, Message message, Channel channel) throws IOException {
        log.info("listen msg: releaseCouponRecord content: {}", productMessage);
        long msgTag = message.getMessageProperties().getDeliveryTag();

        boolean flag = productService.releaseProductStock(productMessage);

        // prevent same unlock task enter in concurrency
        // if parallel consume, don't need lock
        Lock lock = redissonClient.getLock("lock:coupon_record_release:" + productMessage.getTaskId());
        lock.lock();

        try {
            if (flag) {
                channel.basicAck(msgTag, false);
            } else {
                log.error("release product fail, flag=false, {}", productMessage);
                channel.basicReject(msgTag, true);
            }
        } catch (IOException e) {
            log.error("release product error:{}, msg:{}", e, productMessage);
            channel.basicReject(msgTag, true);
        } finally {
            lock.unlock();
        }
    }
}
