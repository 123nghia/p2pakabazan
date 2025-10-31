package com.akabazan.service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class TradeResult {
    private UUID id;
    private UUID orderId;
    private UUID buyerId;
    private UUID sellerId;
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
    private UUID sellerFiatAccountId;
    private String sellerBankName;
    private String sellerAccountNumber;
    private String sellerAccountHolder;
    private String sellerBankBranch;
    private String sellerPaymentType;
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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(UUID buyerId) {
        this.buyerId = buyerId;
    }

    public UUID getSellerId() {
        return sellerId;
    }

    public void setSellerId(UUID sellerId) {
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

    public UUID getSellerFiatAccountId() {
        return sellerFiatAccountId;
    }

    public void setSellerFiatAccountId(UUID sellerFiatAccountId) {
        this.sellerFiatAccountId = sellerFiatAccountId;
    }

    public String getSellerBankName() {
        return sellerBankName;
    }

    public void setSellerBankName(String sellerBankName) {
        this.sellerBankName = sellerBankName;
    }

    public String getSellerAccountNumber() {
        return sellerAccountNumber;
    }

    public void setSellerAccountNumber(String sellerAccountNumber) {
        this.sellerAccountNumber = sellerAccountNumber;
    }

    public String getSellerAccountHolder() {
        return sellerAccountHolder;
    }

    public void setSellerAccountHolder(String sellerAccountHolder) {
        this.sellerAccountHolder = sellerAccountHolder;
    }

    public String getSellerBankBranch() {
        return sellerBankBranch;
    }

    public void setSellerBankBranch(String sellerBankBranch) {
        this.sellerBankBranch = sellerBankBranch;
    }

    public String getSellerPaymentType() {
        return sellerPaymentType;
    }

    public void setSellerPaymentType(String sellerPaymentType) {
        this.sellerPaymentType = sellerPaymentType;
    }
}
