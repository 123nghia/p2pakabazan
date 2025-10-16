package com.akabazan.api.request;

import jakarta.validation.constraints.*;

public class TradeRequest {
    @NotNull(message = "Order ID is required")
    @Positive(message = "Order ID must be positive")
    private Long orderId;
    
    private String type;
    
    @Positive(message = "Fiat account id must be positive")
    private Long fiatAccountId;

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;
    
    // Optional: chat message on creation
    @Size(max = 500, message = "Chat message cannot exceed 500 characters")
    private String chatMessage;
    // Getters & Setters
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getChatMessage() { return chatMessage; }
    public void setChatMessage(String chatMessage) { this.chatMessage = chatMessage; }

    public Long getFiatAccountId() {
        return fiatAccountId;
    }

    public void setFiatAccountId(Long fiatAccountId) {
        this.fiatAccountId = fiatAccountId;
    }
}
