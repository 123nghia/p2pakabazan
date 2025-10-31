package com.akabazan.api.reponse;

import java.time.LocalDateTime;
import java.util.UUID;

public class TradeInfoResponse {

    private UUID tradeId;
    private String tradeCode;
    private String orderType;
    private String status;
    private Double amount;
    private String role;
    private boolean canCancel;
    private UUID sellerFiatAccountId;
    private String bankName;
    private String accountNumber;
    private String accountHolder;
    private String bankBranch;
    private String paymentType;
    private LocalDateTime autoCancelAt;
    private Long timeRemainingSeconds;
    private double price;

    public UUID getTradeId() {
        return tradeId;
    }

    public void setTradeId(UUID tradeId) {
        this.tradeId = tradeId;
    }

    public String getTradeCode() {
        return tradeCode;
    }

    public void setTradeCode(String tradeCode) {
        this.tradeCode = tradeCode;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
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

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public void setAccountHolder(String accountHolder) {
        this.accountHolder = accountHolder;
    }

    public String getBankBranch() {
        return bankBranch;
    }

    public void setBankBranch(String bankBranch) {
        this.bankBranch = bankBranch;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public LocalDateTime getAutoCancelAt() {
        return autoCancelAt;
    }

    public void setAutoCancelAt(LocalDateTime autoCancelAt) {
        this.autoCancelAt = autoCancelAt;
    }

    public Long getTimeRemainingSeconds() {
        return timeRemainingSeconds;
    }

    public void setTimeRemainingSeconds(Long timeRemainingSeconds) {
        this.timeRemainingSeconds = timeRemainingSeconds;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
