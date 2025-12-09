package com.akabazan.common.event;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public class NotificationEvent implements Serializable {

    private UUID userId;
    private UUID notificationId;
    private String type; // NotificationType enum as string
    private String message;
    private Instant occurredAt;

    public NotificationEvent() {
    }

    public NotificationEvent(UUID userId,
                            UUID notificationId,
                            String type,
                            String message,
                            Instant occurredAt) {
        this.userId = userId;
        this.notificationId = notificationId;
        this.type = type;
        this.message = message;
        this.occurredAt = occurredAt;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(UUID notificationId) {
        this.notificationId = notificationId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }
}

