package com.akabazan.websocket.listener;

import com.akabazan.common.event.ChatMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class ChatEventListener {

    private static final Logger log = LoggerFactory.getLogger(ChatEventListener.class);
    private final SimpMessagingTemplate messagingTemplate;

    public ChatEventListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @RabbitListener(queues = "${app.rabbitmq.websocket.chat.queue:websocket.chat.messages.queue}")
    public void handleChatEvent(ChatMessageEvent event) {
        if (event == null || event.getTradeId() == null) {
            return;
        }
        log.info("Received chat message event: tradeId={}, chatId={}, senderId={}, isSystem={}",
                event.getTradeId(), event.getChatId(), event.getSenderId(), event.isSystemMessage());

        // Push to trade-specific chat topic
        String topic = "/topic/trades/" + event.getTradeId() + "/chat";
        messagingTemplate.convertAndSend(topic, event);
    }
}

