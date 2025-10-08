package com.akabazan.api.reponse;

import java.time.LocalDateTime;

public class TradeInfoResponse {
    private Long tradeId;
    private String tradeCode;
    private String orderType;
    private String status;
    private Double amount;
       private String role;
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
    private Long sellerFiatAccountId;
    private String bankName;
    private String accountNumber;
    private String accountHolder;
    private String bankBranch;
    private String paymentType;


    // --- TTL ---
    private LocalDateTime autoCancelAt;
    private Long timeRemainingSeconds;

    private double Price;


    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    // getters/setters
    public Long getTradeId() {
        return tradeId;
    }

    public void setTradeId(Long tradeId) {
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

    public Long getSellerFiatAccountId() {
        return sellerFiatAccountId;
    }

    public void setSellerFiatAccountId(Long sellerFiatAccountId) {
        this.sellerFiatAccountId = sellerFiatAccountId;
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


       public LocalDateTime getAutoCancelAt() { return autoCancelAt; }
    public void setAutoCancelAt(LocalDateTime autoCancelAt) { this.autoCancelAt = autoCancelAt; }
    public Long getTimeRemainingSeconds() { return timeRemainingSeconds; }
    public void setTimeRemainingSeconds(Long timeRemainingSeconds) { this.timeRemainingSeconds = timeRemainingSeconds; }
}
