package com.akabazan.service.event;

import com.akabazan.common.event.ChatMessageEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ChatEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routingKey;

    public ChatEventPublisher(RabbitTemplate rabbitTemplate,
                               @Value("${app.rabbitmq.websocket.exchange:websocket.events.exchange}") String exchange,
                               @Value("${app.rabbitmq.websocket.chat.routing-key:chat.message}") String routingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    public void publish(ChatMessageEvent event) {
        if (event == null) {
            return;
        }
        // Use different routing key for system messages
        String actualRoutingKey = event.isSystemMessage() ? "chat.system" : routingKey;
        rabbitTemplate.convertAndSend(exchange, actualRoutingKey, event);
    }
}

