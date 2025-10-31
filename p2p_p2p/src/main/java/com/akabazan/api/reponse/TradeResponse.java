package com.akabazan.api.reponse;

import java.time.LocalDateTime;
import java.util.UUID;

public class TradeResponse {

    private UUID id;
    private UUID orderId;
    private UUID buyerId;
    private UUID sellerId;
    private double amount;
    private double price;
    private String status;
    private String tradeCode;
    private String fiat;
    private String token;
    private String counterparty;
    private String role;
    private boolean canCancel;
    private UUID sellerFiatAccountId;
    private String sellerBankName;
    private String sellerAccountNumber;
    private String sellerAccountHolder;
    private String sellerBankBranch;
    private String sellerPaymentType;
    private String senderUserName;
    private String buyerUserName;
    private LocalDateTime createdAt;

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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
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

    public String getFiat() {
        return fiat;
    }

    public void setFiat(String fiat) {
        this.fiat = fiat;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCounterparty() {
        return counterparty;
    }

    public void setCounterparty(String counterparty) {
        this.counterparty = counterparty;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isCanCancel() {
        return canCancel;
    }

    public void setCanCancel(boolean canCancel) {
        this.canCancel = canCancel;
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

    public String getSenderUserName() {
        return senderUserName;
    }

    public void setSenderUserName(String senderUserName) {
        this.senderUserName = senderUserName;
    }

    public String getBuyerUserName() {
        return buyerUserName;
    }

    public void setBuyerUserName(String buyerUserName) {
        this.buyerUserName = buyerUserName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
