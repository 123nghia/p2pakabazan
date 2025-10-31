package com.akabazan.repository.entity;

import com.akabazan.framework.data.domain.AuditEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "users_wallets")
public class UsersWallets extends AuditEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token", nullable = false, length = 50)
    private String token;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Column(name = "balance", nullable = false)
    private Double balance;

    // Getters and setters
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Double getBalance() { return balance; }
    public void setBalance(Double balance) { this.balance = balance; }
}
