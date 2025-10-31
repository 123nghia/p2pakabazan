package com.akabazan.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

public class OrderRequest {

    @NotBlank(message = "Order type is required")
    @Pattern(regexp = "^(BUY|SELL)$", message = "Order type must be BUY or SELL")
    private String type;

    @NotBlank(message = "Token is required")
    private String token;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private Double price;

    @NotBlank(message = "Fiat currency is required")
    private String fiat;

    @NotBlank(message = "Price mode is required")
    @Pattern(regexp = "^(MARKET|CUSTOM)$", message = "Price mode must be MARKET or CUSTOM")
    private String priceMode;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    @NotNull(message = "Fiat account id is required")
    private UUID fiatAccountId;

    @Positive(message = "Min limit must be positive")
    private Double minLimit;

    @Positive(message = "Max limit must be positive")
    private Double maxLimit;

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

    public UUID getFiatAccountId() {
        return fiatAccountId;
    }

    public void setFiatAccountId(UUID fiatAccountId) {
        this.fiatAccountId = fiatAccountId;
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
}
