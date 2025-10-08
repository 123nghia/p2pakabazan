package com.akabazan.service.dto;

import java.time.LocalDateTime;

public class TradeInfoResult {
    private Long tradeId;
    private String tradeCode;
    private String orderType; // "BUY" | "SELL"
    private String status;    // enum name, ví dụ: PENDING/PAID/COMPLETED/CANCELLED
    private Double amount;


    private Long sellerFiatAccountId;
    private String bankName;
    private String accountNumber;
    private String accountHolder;
    private String bankBranch;
    private String paymentType;
    private double Price;
    private boolean canCancel;

public boolean isCanCancel() {
    return canCancel;
}

public void setCanCancel(boolean canCancel) {
    this.canCancel = canCancel;
}
  
    public double getPrice() {
      return Price;
    }
    public void setPrice(double price) {
      Price = price;
    }
    // --- thêm các trường TTL ---
    private LocalDateTime autoCancelAt;
    private Long timeRemainingSeconds;

     private String role;

    public String getRole() {
      return role;
    }
     public void setRole(String role) {
       this.role = role;
     }
    // getters/setters
    public Long getTradeId() { return tradeId; }
    public void setTradeId(Long tradeId) { this.tradeId = tradeId; }
    public String getTradeCode() { return tradeCode; }
    public void setTradeCode(String tradeCode) { this.tradeCode = tradeCode; }
    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; } 

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public String getAccountHolder() { return accountHolder; }
    public void setAccountHolder(String accountHolder) { this.accountHolder = accountHolder; }
    public String getBankBranch() { return bankBranch; }
    public void setBankBranch(String bankBranch) { this.bankBranch = bankBranch; }
    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }
    public Long getSellerFiatAccountId() { return sellerFiatAccountId; }
    public void setSellerFiatAccountId(Long sellerFiatAccountId) { this.sellerFiatAccountId = sellerFiatAccountId; }

    public LocalDateTime getAutoCancelAt() { return autoCancelAt; }
    public void setAutoCancelAt(LocalDateTime autoCancelAt) { this.autoCancelAt = autoCancelAt; }
    public Long getTimeRemainingSeconds() { return timeRemainingSeconds; }
    public void setTimeRemainingSeconds(Long timeRemainingSeconds) { this.timeRemainingSeconds = timeRemainingSeconds; }
}
