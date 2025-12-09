package com.akabazan.notification.event;

import com.akabazan.common.event.NotificationEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routingKey;

    public NotificationEventPublisher(RabbitTemplate rabbitTemplate,
                                      @Value("${app.rabbitmq.websocket.exchange:websocket.events.exchange}") String exchange,
                                      @Value("${app.rabbitmq.websocket.notification.routing-key:notification.new}") String routingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    public void publish(NotificationEvent event) {
        if (event == null) {
            return;
        }
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }
}

