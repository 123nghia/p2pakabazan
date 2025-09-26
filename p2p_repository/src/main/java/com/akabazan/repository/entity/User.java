package com.akabazan.repository.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User extends AbstractEntity { // Extend AbstractEntity
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "kyc_status", nullable = false)
    private KycStatus kycStatus = KycStatus.UNVERIFIED;
    @ElementCollection
    @CollectionTable(name = "user_wallets", joinColumns = @JoinColumn(name = "user_id"))
    private List<Wallet> wallets = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "user_login_history", joinColumns = @JoinColumn(name = "user_id"))
    private List<LoginHistory> loginHistory = new ArrayList<>();

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public KycStatus getKycStatus() { return kycStatus; }
    public void setKycStatus(KycStatus kycStatus) { this.kycStatus = kycStatus; }
    public List<Wallet> getWallets() { return wallets; }
    public void setWallets(List<Wallet> wallets) { this.wallets = wallets; }
    public List<LoginHistory> getLoginHistory() { return loginHistory; }
    public void setLoginHistory(List<LoginHistory> loginHistory) { this.loginHistory = loginHistory; }

    public enum KycStatus {
        UNVERIFIED, PENDING, VERIFIED, REJECTED
    }

    @Embeddable
    public static class Wallet {
        private String token;
        private String address;
        private double balance;

        // Getters and Setters
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public double getBalance() { return balance; }
        public void setBalance(double balance) { this.balance = balance; }
    }

    @Embeddable
    public static class LoginHistory {
        private String ip;
        private String device;
        private java.time.LocalDateTime timestamp;

        // Getters and Setters
        public String getIp() { return ip; }
        public void setIp(String ip) { this.ip = ip; }
        public String getDevice() { return device; }
        public void setDevice(String device) { this.device = device; }
        public java.time.LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(java.time.LocalDateTime timestamp) { this.timestamp = timestamp; }
    }
}