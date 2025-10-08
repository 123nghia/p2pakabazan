package com.akabazan.api.reponse;

import java.time.LocalDateTime;

public class TradeResponse {
    private Long id;
    private Long orderId;
    private Long buyerId;
    private Long sellerId;
    private double amount;
    private String status;
    // private boolean escrow;
    private String tradeCode;
    private String Fiat;
    private String Token;
    private String Counterparty;
    private Long sellerFiatAccountId;
    private String sellerBankName;
    private String sellerAccountNumber;
    private String sellerAccountHolder;
    private String sellerBankBranch;
    private String sellerPaymentType;

    private boolean canCancel;

public boolean isCanCancel() {
    return canCancel;
}

public void setCanCancel(boolean canCancel) {
    this.canCancel = canCancel;
}
     private String role;


    public String getRole() {
      return role;
    }
     public void setRole(String role) {
       this.role = role;
     }

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

    public String getCounterparty() {
        return Counterparty;
    }

    public void setCounterparty(String partnerUserName) {
        this.Counterparty = partnerUserName;
    }

     private double price;
    private String SenderUserName;

    private String BuyerUserName;
    public String getBuyerUserName() {
        return BuyerUserName;
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

   

    public String getTradeCode() {
        return tradeCode;
    }

    public void setTradeCode(String tradeCode) {
        this.tradeCode = tradeCode;
    }

    public Long getSellerFiatAccountId() {
        return sellerFiatAccountId;
    }

    public void setSellerFiatAccountId(Long sellerFiatAccountId) {
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
