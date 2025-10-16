package com.akabazan.service.command;

public class OrderCreateCommand {

    private String type;
    private String token;
    private Double amount;
    private Double price;
    private String fiat;
    private Double minLimit;
    private Double maxLimit;
    private String priceMode;
    private String paymentMethod;
    private Long fiatAccountId;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getFiat() {
        return fiat;
    }

    public void setFiat(String fiat) {
        this.fiat = fiat;
    }

    public Double getMinLimit() {
        return minLimit;
    }

    public void setMinLimit(Double minLimit) {
        this.minLimit = minLimit;
    }

    public Double getMaxLimit() {
        return maxLimit;
    }

    public void setMaxLimit(Double maxLimit) {
        this.maxLimit = maxLimit;
    }

    public String getPriceMode() {
        return priceMode;
    }

    public void setPriceMode(String priceMode) {
        this.priceMode = priceMode;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Long getFiatAccountId() {
        return fiatAccountId;
    }

    public void setFiatAccountId(Long fiatAccountId) {
        this.fiatAccountId = fiatAccountId;
    }
}
