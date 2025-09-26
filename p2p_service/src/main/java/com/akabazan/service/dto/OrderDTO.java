package com.akabazan.service.dto;

import java.time.LocalDateTime;

public class OrderDTO {
    private Long id;
    private Long userId;
    private String type;          // BUY / SELL
    private String token;         // USDT, BTC,...
    private double amount;
    private double price;
    private String paymentMethod;
    private double minLimit;
    private double maxLimit;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public double getMinLimit() { return minLimit; }
    public void setMinLimit(double minLimit) { this.minLimit = minLimit; }

    public double getMaxLimit() { return maxLimit; }
    public void setMaxLimit(double maxLimit) { this.maxLimit = maxLimit; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
