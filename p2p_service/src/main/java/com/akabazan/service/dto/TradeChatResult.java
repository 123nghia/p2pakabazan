package com.akabazan.service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class TradeChatResult {
    private UUID id;
    private UUID tradeId;
    private UUID senderId;
    private String message;
    private LocalDateTime timestamp;
    private String recipientRole;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTradeId() {
        return tradeId;
    }

    public void setTradeId(UUID tradeId) {
        this.tradeId = tradeId;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public void setSenderId(UUID senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getRecipientRole() {
        return recipientRole;
    }

    public void setRecipientRole(String recipientRole) {
        this.recipientRole = recipientRole;
    }
}
