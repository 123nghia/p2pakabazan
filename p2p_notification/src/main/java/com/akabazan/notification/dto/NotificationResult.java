package com.akabazan.notification.dto;

import com.akabazan.notification.enums.NotificationType;
import java.time.LocalDateTime;
import java.util.UUID;

public class NotificationResult {

    private UUID id;
    private String message;
    private boolean read;
    private LocalDateTime createdAt;
    private NotificationType type;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
