package com.akabazan.service.command;

import java.util.UUID;


public class TradeCreateCommand {

    private UUID orderId;
    private double amount;
    private String chatMessage;
    private UUID fiatAccountId;
    
    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(String chatMessage) {
        this.chatMessage = chatMessage;
    }

    public UUID getFiatAccountId() {
        return fiatAccountId;
    }

    public void setFiatAccountId(UUID fiatAccountId) {
        this.fiatAccountId = fiatAccountId;
    }
}
