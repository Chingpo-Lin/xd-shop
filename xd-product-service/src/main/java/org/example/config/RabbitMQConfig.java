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
    @Value("${mqconfig.stock_event_exchange}")
    private String eventExchange;

    /**
     * first delay queue
     */
    @Value("${mqconfig.stock_release_delay_queue}")
    private String stockReleaseDelayQueue;
    /**
     * first queue routing key
     * routing key for enter queue
     */
    @Value("${mqconfig.stock_release_delay_routing_key}")
    private String stockReleaseDelayRoutingKey;

    /**
     * second queue, listened for recover stock */
    @Value("${mqconfig.stock_release_queue}")
    private String stockReleaseQueue;

    /**
     * second queue routing key
     * routing key for enter dead letter queue
     */
    @Value("${mqconfig.stock_release_routing_key}")
    private String stockReleaseRoutingKey;

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
    public Exchange stockEventExchange() {
        return new TopicExchange(eventExchange, true, false);
    }

    /**
     * delay queue
     */
    @Bean
    public Queue stockReleaseDelayQueue() {

        Map<String, Object> args = new HashMap<>(3);
        args.put("x-message-ttl", ttl);
        args.put("x-dead-letter-routing-key", stockReleaseRoutingKey);
        args.put("x-dead-letter-exchange", eventExchange);

        return new Queue(stockReleaseDelayQueue, true, false, false, args);
    }

    /**
     * dead letter queue, used for to be listened
     */
    @Bean
    public Queue stockReleaseQueue() {
        return new Queue(stockReleaseQueue, true, false, false);
    }

    /**
     * delayed queue bind relation
     * @return
     */
    @Bean
    public Binding stockReleaseDelayBinding() {
        return new Binding(stockReleaseDelayQueue, Binding.DestinationType.QUEUE, eventExchange, stockReleaseDelayRoutingKey, null);
    }

    /**
     * dead letter queue bind relation
     * @return
     */
    @Bean
    public Binding stockReleaseBinding() {
        return new Binding(stockReleaseQueue, Binding.DestinationType.QUEUE, eventExchange, stockReleaseRoutingKey, null);
    }
}
