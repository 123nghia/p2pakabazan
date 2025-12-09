package com.akabazan.common.event;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public class ChatMessageEvent implements Serializable {

    private UUID tradeId;
    private UUID chatId;
    private UUID senderId;
    private String message;
    private String recipientRole; // BUYER, SELLER, ALL
    private boolean isSystemMessage;
    private Instant timestamp;
    private Instant occurredAt;

    public ChatMessageEvent() {
    }

    public ChatMessageEvent(UUID tradeId,
                           UUID chatId,
                           UUID senderId,
                           String message,
                           String recipientRole,
                           boolean isSystemMessage,
                           Instant timestamp,
                           Instant occurredAt) {
        this.tradeId = tradeId;
        this.chatId = chatId;
        this.senderId = senderId;
        this.message = message;
        this.recipientRole = recipientRole;
        this.isSystemMessage = isSystemMessage;
        this.timestamp = timestamp;
        this.occurredAt = occurredAt;
    }

    public UUID getTradeId() {
        return tradeId;
    }

    public void setTradeId(UUID tradeId) {
        this.tradeId = tradeId;
    }

    public UUID getChatId() {
        return chatId;
    }

    public void setChatId(UUID chatId) {
        this.chatId = chatId;
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

    public String getRecipientRole() {
        return recipientRole;
    }

    public void setRecipientRole(String recipientRole) {
        this.recipientRole = recipientRole;
    }

    public boolean isSystemMessage() {
        return isSystemMessage;
    }

    public void setSystemMessage(boolean systemMessage) {
        isSystemMessage = systemMessage;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }
}

