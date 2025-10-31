package com.akabazan.repository.entity;

import com.akabazan.framework.data.domain.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "trade_chats")
public class TradeChat extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "trade_id", nullable = false)
    private Trade trade;

    @Column(name = "sender_id")
    private UUID senderId;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "recipient_role")
    private String recipientRole;

    // Getters & Setters
    public Trade getTrade() { return trade; }
    public void setTrade(Trade trade) { this.trade = trade; }
    public UUID getSenderId() { return senderId; }
    public void setSenderId(UUID senderId) { this.senderId = senderId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getRecipientRole() { return recipientRole; }
    public void setRecipientRole(String recipientRole) { this.recipientRole = recipientRole; }
}
