package com.akabazan.api.request;

public class TradeRequest {
    private Long orderId;
    private String type;
    private String bankName;
    private String accountNumber;
    private String accountHolder;
    private String branch;
    private String paymentType;
    private Long fiatAccountId;

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

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public Long getFiatAccountId() {
        return fiatAccountId;
    }

    public void setFiatAccountId(Long fiatAccountId) {
        this.fiatAccountId = fiatAccountId;
    }
}
