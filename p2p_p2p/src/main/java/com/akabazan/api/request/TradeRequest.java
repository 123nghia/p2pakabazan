package com.akabazan.api.request;

public class TradeRequest {
    private Long orderId;
    private String type;
   
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    private Double amount;
    // Optional: chat message on creation
    private String chatMessage;
    // Getters & Setters
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getChatMessage() { return chatMessage; }
    public void setChatMessage(String chatMessage) { this.chatMessage = chatMessage; }
}
