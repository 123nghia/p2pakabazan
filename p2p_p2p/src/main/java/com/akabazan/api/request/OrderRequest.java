package com.akabazan.api.request;

public class OrderRequest {
    private String type; // BUY / SELL
    private String token; // BTC, USDT, ETH
    private Double amount; // số token
    private Double price; // giá 1 token theo fiat
    private String paymentMethod; // BANK_TRANSFER, MOMO, ZALOPAY...
    private String fiatAccount; // tài khoản nhận fiat
    private Double minLimit;
    private Double maxLimit;

    // Getters & Setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getFiatAccount() { return fiatAccount; }
    public void setFiatAccount(String fiatAccount) { this.fiatAccount = fiatAccount; }
    public Double getMinLimit() { return minLimit; }
    public void setMinLimit(Double minLimit) { this.minLimit = minLimit; }
    public Double getMaxLimit() { return maxLimit; }
    public void setMaxLimit(Double maxLimit) { this.maxLimit = maxLimit; }
}