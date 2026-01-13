package com.akabazan.repository.entity;

import com.akabazan.framework.data.domain.AuditEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.akabazan.repository.constant.OrderStatus;

@Entity
@Table(name = "orders")
public class Order extends AuditEntity {

    @Column(nullable = false)
    private String type; // BUY / SELL

    @Column(nullable = false)
    private String token; // BTC, USDT, ETH

    @Column(nullable = false)
    private Double amount; // số token

    @Column(nullable = false)
    private Double price; // giá 1 token theo fiat

    @Column(nullable = false, length = 10)
    private String fiat; // Ví dụ: VND, USD

    @Column(nullable = false)
    private Double minLimit = 0.0;

    @Column(nullable = false)
    private Double maxLimit = 100.00;

    @Column(nullable = false)
    private String status = OrderStatus.OPEN.name(); // OPEN, CLOSED, CANCELLED

    @Column
    private String paymentMethod; // Bank transfer, MoMo, PayPal...

    @Column(name = "price_mode", nullable = false)
    private String priceMode = "CUSTOM";

    @ManyToOne
    @JoinColumn(name = "fiat_account_id")
    private FiatAccount fiatAccount; // Tài khoản ngân hàng/ví được liên kết

    private Double availableAmount; // Số lượng còn lại có thể giao dịch

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "funds_lock_id")
    private String fundsLockId;

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

    // Getters & Setters
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public FiatAccount getFiatAccount() {
        return fiatAccount;
    }

    public void setFiatAccount(FiatAccount fiatAccount) {
        this.fiatAccount = fiatAccount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Double getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(Double availableAmount) {
        this.availableAmount = availableAmount;
    }

    public String getFundsLockId() {
        return fundsLockId;
    }

    public void setFundsLockId(String fundsLockId) {
        this.fundsLockId = fundsLockId;
    }

    public LocalDateTime getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(LocalDateTime expireAt) {
        this.expireAt = expireAt;
    }

    public String getPriceMode() {
        return priceMode;
    }

    public void setPriceMode(String priceMode) {
        this.priceMode = priceMode;
    }
}
