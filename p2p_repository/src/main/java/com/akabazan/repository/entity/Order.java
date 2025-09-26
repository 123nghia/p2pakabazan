package com.akabazan.repository.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class Order extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Column(name = "min_limit", nullable = false)
    private double minLimit;

    @Column(name = "max_limit", nullable = false)
    private double maxLimit;

    @Column(name = "status", nullable = false)
    private String status;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
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
}