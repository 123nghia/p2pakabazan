package com.akabazan.repository.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.akabazan.repository.constant.OrderStatus;

@Entity
@Table(name = "orders")
public class Order extends AbstractEntity {

    @Column(nullable = false)
    private String type; // BUY / SELL

    @Column(nullable = false)
    private String token; // BTC, USDT, ETH

    @Column(nullable = false)
    private Double amount; // số token

    @Column(nullable = false)
    private Double price; // giá 1 token theo fiat

    @Column(nullable = false)
    private Double minLimit;

    @Column(nullable = false)
    private Double maxLimit;

    @Column(nullable = false)
    private String status = OrderStatus.OPEN.name(); // OPEN, CLOSED, CANCELLED

    @Column
    private String paymentMethod;

    @Column
    private String fiatAccount;

     private Double availableAmount; // Số lượng còn lại có thể giao dịch

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Tổng fiat = amount * price
    public double getTotalFiat() {
        return this.amount * this.price;
    }

     @Column(name = "expire_at")
    private LocalDateTime expireAt;

        @PrePersist
       public void prePersist() {
        if (availableAmount == null) {
            availableAmount = amount; // Lúc tạo mới = amount ban đầu
        }
        if (expireAt == null) {
            expireAt = LocalDateTime.now().plusMinutes(15); // mặc định expire sau 15 phút
        }
    }
      public LocalDateTime getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(LocalDateTime expireAt) {
        this.expireAt = expireAt;
    }

    // Getters & Setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Double getMinLimit() { return minLimit; }
    public void setMinLimit(Double minLimit) { this.minLimit = minLimit; }
    public Double getMaxLimit() { return maxLimit; }
    public void setMaxLimit(Double maxLimit) { this.maxLimit = maxLimit; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getFiatAccount() { return fiatAccount; }
    public void setFiatAccount(String fiatAccount) { this.fiatAccount = fiatAccount; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }


    public Double getAvailableAmount() { return availableAmount; }
    public void setAvailableAmount(Double availableAmount) { this.availableAmount = availableAmount; }

}
