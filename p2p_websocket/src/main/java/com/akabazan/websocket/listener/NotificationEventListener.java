package com.akabazan.websocket.listener;

import com.akabazan.common.event.NotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationEventListener.class);
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationEventListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @RabbitListener(queues = "${app.rabbitmq.websocket.notification.queue:websocket.notifications.queue}")
    public void handleNotificationEvent(NotificationEvent event) {
        if (event == null || event.getUserId() == null) {
            return;
        }
        log.info("Received notification event: userId={}, notificationId={}, type={}",
                event.getUserId(), event.getNotificationId(), event.getType());

        // Push to user-specific notification topic
        String topic = "/topic/users/" + event.getUserId() + "/notifications";
        messagingTemplate.convertAndSend(topic, event);
    }
}

