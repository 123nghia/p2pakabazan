package com.akabazan.service.event;

import com.akabazan.common.event.TradeStatusEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TradeEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routingKey;

    public TradeEventPublisher(RabbitTemplate rabbitTemplate,
                               @Value("${app.rabbitmq.trade.exchange:trade.events.exchange}") String exchange,
                               @Value("${app.rabbitmq.trade.routing-key:trade.events.status}") String routingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    public void publish(TradeStatusEvent event) {
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }
}

