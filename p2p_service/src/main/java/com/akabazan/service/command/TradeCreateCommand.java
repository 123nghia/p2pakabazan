package com.akabazan.service.command;

public class TradeCreateCommand {

    private Long orderId;
    private double amount;
    private String chatMessage;
    private Long fiatAccountId;
    
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
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

    public Long getFiatAccountId() {
        return fiatAccountId;
    }

    public void setFiatAccountId(Long fiatAccountId) {
        this.fiatAccountId = fiatAccountId;
    }
}
