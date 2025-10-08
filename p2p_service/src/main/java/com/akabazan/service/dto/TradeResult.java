package com.akabazan.service.dto;

import java.time.LocalDateTime;

public class TradeResult {
    private Long id;
    private Long orderId;
    private Long buyerId;
    private Long sellerId;
    private double amount;
    private double price;
    private String SenderUserName;
    private String role;

    private String Counterparty;

    private boolean canCancel;

public boolean isCanCancel() {
    return canCancel;
}

public void setCanCancel(boolean canCancel) {
    this.canCancel = canCancel;
}

    public String getRole() {
      return role;
    }
     public void setRole(String role) {
       this.role = role;
     }

    public String getCounterparty() {
        return Counterparty;
    }

    public void setCounterparty(String partnerUserName) {
        this.Counterparty = partnerUserName;
    }

    private String BuyerUserName;
    public String getBuyerUserName() {
        return BuyerUserName;
    }

     private String Fiat;
    private String Token;

     public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

     public String getFiat() {
        return Fiat;
    }

    public void setFiat(String fiat) {
        Fiat = fiat;
    }

    public void setBuyerUserName(String buyerUserName) {
        BuyerUserName = buyerUserName;
    }

    public String getSenderUserName() {
        return SenderUserName;
    }

    public void setSenderUserName(String senderUserName) {
        SenderUserName = senderUserName;
    }

    private LocalDateTime createdAt;
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    private String status;
    private boolean escrow;
    private String tradeCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isEscrow() {
        return escrow;
    }

    public void setEscrow(boolean escrow) {
        this.escrow = escrow;
    }

    public String getTradeCode() {
        return tradeCode;
    }

    public void setTradeCode(String tradeCode) {
        this.tradeCode = tradeCode;
    }
}
