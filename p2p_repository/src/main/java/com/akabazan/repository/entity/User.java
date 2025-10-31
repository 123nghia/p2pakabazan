package com.akabazan.repository.entity;

import com.akabazan.framework.data.domain.AuditEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User extends AuditEntity {

    @Column(nullable = false)
    private String email;

    @Column
    private String phone;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "kyc_status", nullable = false)
    private KycStatus kycStatus = KycStatus.UNVERIFIED;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = Role.TRADER;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Wallet> wallets = new ArrayList<>();

    // Getters / Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public KycStatus getKycStatus() { return kycStatus; }
    public void setKycStatus(KycStatus kycStatus) { this.kycStatus = kycStatus; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public List<Wallet> getWallets() { return wallets; }
    public void setWallets(List<Wallet> wallets) { this.wallets = wallets; }

    public enum KycStatus {
        UNVERIFIED, PENDING, VERIFIED, REJECTED
    }

    public enum Role {
        TRADER, MODERATOR, ADMIN
    }
}
