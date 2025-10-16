package com.akabazan.repository.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "wallets")
public class Wallet  extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;
    private String address;
    @Column(nullable = false)
    private double balance;
    // @Column(name = "available_balance", nullable = false)
    private Double available_balance; // tổng số dư

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Getters / Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public void setAvailableBalance(double aVailableBalance)
     { this.available_balance = aVailableBalance; }
    public double getAvailableBalance() { 
        if (available_balance == null || available_balance < 0)
        {
             setAvailableBalance(0.0);
             return 0.0;
        }
        return available_balance;
     }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
