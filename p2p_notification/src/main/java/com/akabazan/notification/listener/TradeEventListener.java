package com.akabazan.notification.listener;

import com.akabazan.common.event.TradeStatusEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class TradeEventListener {

    private static final Logger log = LoggerFactory.getLogger(TradeEventListener.class);
    private final SimpMessagingTemplate messagingTemplate;

    public TradeEventListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @RabbitListener(queues = "${app.rabbitmq.trade.queue:trade.events.queue}")
    public void handleTradeEvent(TradeStatusEvent event) {
        if (event == null) {
            return;
        }
        log.info("Received trade status event: tradeId={}, orderId={}, status={}, buyerId={}, sellerId={}",
                event.getTradeId(), event.getOrderId(), event.getStatus(), event.getBuyerId(), event.getSellerId());

        messagingTemplate.convertAndSend("/topic/trades", event);
        if (event.getTradeId() != null) {
            messagingTemplate.convertAndSend("/topic/trades/" + event.getTradeId(), event);
        }
        if (event.getBuyerId() != null) {
            messagingTemplate.convertAndSend("/topic/users/" + event.getBuyerId() + "/trades", event);
        }
        if (event.getSellerId() != null) {
            messagingTemplate.convertAndSend("/topic/users/" + event.getSellerId() + "/trades", event);
        }
    }
}
