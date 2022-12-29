package org.example.mq;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.example.model.CouponRecordMessage;
import org.example.service.CouponRecordService;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.locks.Lock;

@Slf4j
@Component
@RabbitListener(queues = "${mqconfig.coupon_release_queue}")
public class CouponMQListener {

    @Autowired
    private CouponRecordService couponRecordService;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * need to check:
     * redundant consume
     * retry times after consume fail
     * or
     * record log and insert to db after fail instead of requeue
     *
     * @param recordMessage
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitHandler
    public void releaseCouponRecord(CouponRecordMessage recordMessage, Message message, Channel channel) throws IOException {
        log.info("listen msg: releaseCouponRecord content: {}", recordMessage);
        long msgTag = message.getMessageProperties().getDeliveryTag();

        boolean flag = couponRecordService.releaseCouponRecord(recordMessage);

        // prevent same unlock task enter in concurrency
        // if parallel consume, don't need lock
        Lock lock = redissonClient.getLock("lock:coupon_record_release:" + recordMessage.getTaskId());
        lock.lock();

        try {
            if (flag) {
                channel.basicAck(msgTag, false);
            } else {
                log.error("release coupon fail, flag=false, {}", recordMessage);
                channel.basicReject(msgTag, true);
            }
        } catch (IOException e) {
            log.error("release coupon record error:{}, msg:{}", e, recordMessage);
            channel.basicReject(msgTag, true);
        } finally {

        }
    }
}
