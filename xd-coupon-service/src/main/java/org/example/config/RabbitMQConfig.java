package org.example.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;

public class RabbitMQConfig {

    /**
     * exchange
     */
    @Value("${mqconfig.coupon_event_exchange}")
    private String eventExchange;

    /**
     * first delay queue，
     */
    @Value("${mqconfig.coupon_release_delay_queue}")
    private String couponReleaseDelayQueue;

    /**
     * first queue routing key
     * routing key for entering queue
     */
    @Value("${mqconfig.coupon_release_delay_routing_key}")
    private String couponReleaseDelayRoutingKey;

    /**
     * second queue, recover stock queue
     */
    @Value("${mqconfig.coupon_release_queue}")
    private String couponReleaseQueue;

    /**
     * routing key for second queue
     *
     * routing key for enter dead letter queue
     */
    @Value("${mqconfig.coupon_release_routing_key}")
    private String couponReleaseRoutingKey;

    /**
     * expire time
     */
    @Value("${mqconfig.ttl}")
    private Integer ttl;

    /**
     * msg converter
     * @return
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * create topic exchange, direct also work
     * generally each microservice has one exchange
     * @return
     */
    @Bean
    public Exchange couponEventExchange() {
        return new TopicExchange(eventExchange, true, false);
    }

    /**
     * delay queue
     */
    @Bean
    public Queue couponReleaseDelayQueue() {

        Map<String, Object> args = new HashMap<>(3);
        args.put("x-message-ttl", ttl);
        args.put("x-dead-letter-routing-key", couponReleaseRoutingKey);
        args.put("x-dead-letter-exchange", eventExchange);

        return new Queue(couponReleaseDelayQueue, true, false, false, args);
    }

    /**
     * dead letter queue, used for to be listened
     */
    @Bean
    public Queue couponReleaseQueue() {
        return new Queue(couponReleaseQueue, true, false, false);
    }

    /**
     * delayed queue bind relation
     * @return
     */
    @Bean
    public Binding couponReleaseDelayBinding() {
        return new Binding(couponReleaseDelayQueue, Binding.DestinationType.QUEUE, eventExchange, couponReleaseDelayRoutingKey, null);
    }

    /**
     * dead letter queue bind relation
     * @return
     */
    @Bean
    public Binding couponReleaseBinding() {
       return new Binding(couponReleaseQueue, Binding.DestinationType.QUEUE, eventExchange, couponReleaseRoutingKey, null);
    }
}
