package com.akabazan.service.event;

import com.akabazan.common.event.TradeStatusEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TradeEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final String tradeExchange;
    private final String tradeRoutingKey;
    private final String websocketExchange;
    private final String websocketRoutingKey;

    public TradeEventPublisher(RabbitTemplate rabbitTemplate,
                               @Value("${app.rabbitmq.trade.exchange:trade.events.exchange}") String tradeExchange,
                               @Value("${app.rabbitmq.trade.routing-key:trade.events.status}") String tradeRoutingKey,
                               @Value("${app.rabbitmq.websocket.exchange:websocket.events.exchange}") String websocketExchange,
                               @Value("${app.rabbitmq.websocket.trade.routing-key:trade.events.status}") String websocketRoutingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.tradeExchange = tradeExchange;
        this.tradeRoutingKey = tradeRoutingKey;
        this.websocketExchange = websocketExchange;
        this.websocketRoutingKey = websocketRoutingKey;
    }

    public void publish(TradeStatusEvent event) {
        rabbitTemplate.convertAndSend(tradeExchange, tradeRoutingKey, event);
        // Fan-out to WebSocket exchange for real-time updates
        if (!tradeExchange.equals(websocketExchange) || !tradeRoutingKey.equals(websocketRoutingKey)) {
            rabbitTemplate.convertAndSend(websocketExchange, websocketRoutingKey, event);
        }
    }
}

