package com.akabazan.service.dto;

public class TradeDTO {
    private Long id;
    private Long orderId;
    private Long buyerId;
    private Long sellerId;
    private double amount;
    private String status;
    private boolean escrow;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getBuyerId() { return buyerId; }
    public void setBuyerId(Long buyerId) { this.buyerId = buyerId; }

    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isEscrow() { return escrow; }
    public void setEscrow(boolean escrow) { this.escrow = escrow; }
}
