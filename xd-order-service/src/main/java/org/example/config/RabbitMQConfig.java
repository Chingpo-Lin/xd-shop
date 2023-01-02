package org.example.config;

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

@Configuration
@Data
public class RabbitMQConfig {


    /**
     * exchange
     */
    @Value("${mqconfig.order_event_exchange}")
    private String eventExchange;

    /**
     * delay queue
     */
    @Value("${mqconfig.order_close_delay_queue}")
    private String orderCloseDelayQueue;

    /**
     * close order queue (dead letter queue)
     */
    @Value("${mqconfig.order_close_queue}")
    private String orderCloseQueue;

    /**
     * routing key for enter delay queue
     */
    @Value("${mqconfig.order_close_delay_routing_key}")
    private String orderCloseDelayRoutingKey;

    /**
     * routing key for enter dead letter queue
     */
    @Value("${mqconfig.order_close_routing_key}")
    private String orderCloseRoutingKey;

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
    public Exchange orderEventExchange() {
        return new TopicExchange(eventExchange, true, false);
    }

    /**
     * delay queue
     */
    @Bean
    public Queue orderCloseDelayQueue() {

        Map<String, Object> args = new HashMap<>(3);
        args.put("x-message-ttl", ttl);
        args.put("x-dead-letter-routing-key", orderCloseRoutingKey);
        args.put("x-dead-letter-exchange", eventExchange);

        return new Queue(orderCloseDelayQueue, true, false, false, args);
    }

    /**
     * dead letter queue, used for to be listened
     */
    @Bean
    public Queue orderCloseQueue() {
        return new Queue(orderCloseQueue, true, false, false);
    }

    /**
     * delayed queue bind relation
     * @return
     */
    @Bean
    public Binding orderCloseDelayBinding() {
        return new Binding(orderCloseDelayQueue, Binding.DestinationType.QUEUE, eventExchange, orderCloseDelayRoutingKey, null);
    }

    /**
     * dead letter queue bind relation
     * @return
     */
    @Bean
    public Binding orderCloseBinding() {
       return new Binding(orderCloseQueue, Binding.DestinationType.QUEUE, eventExchange, orderCloseRoutingKey, null);
    }
}
