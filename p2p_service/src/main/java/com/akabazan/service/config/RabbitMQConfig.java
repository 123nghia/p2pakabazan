package com.akabazan.service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbitmq.trade.exchange:trade.events.exchange}")
    private String tradeExchange;

    @Value("${app.rabbitmq.trade.queue:trade.events.queue}")
    private String tradeQueue;

    @Value("${app.rabbitmq.trade.routing-key:trade.events.status}")
    private String tradeRoutingKey;

    @Bean
    public DirectExchange tradeEventExchange() {
        return new DirectExchange(tradeExchange, true, false);
    }

    @Bean
    public Queue tradeEventQueue() {
        return new Queue(tradeQueue, true);
    }

    @Bean
    public Binding tradeEventBinding(DirectExchange tradeEventExchange, Queue tradeEventQueue) {
        return BindingBuilder.bind(tradeEventQueue).to(tradeEventExchange).with(tradeRoutingKey);
    }
}

