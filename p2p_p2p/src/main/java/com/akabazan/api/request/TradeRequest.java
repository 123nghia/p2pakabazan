package com.akabazan.api.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public class TradeRequest {

    @NotNull(message = "Order ID is required")
    private UUID orderId;

    private String type;

    @NotNull(message = "Fiat account id is required")
    private UUID fiatAccountId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;

    @Size(max = 500, message = "Chat message cannot exceed 500 characters")
    private String chatMessage;

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UUID getFiatAccountId() {
        return fiatAccountId;
    }

    public void setFiatAccountId(UUID fiatAccountId) {
        this.fiatAccountId = fiatAccountId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(String chatMessage) {
        this.chatMessage = chatMessage;
    }
}
