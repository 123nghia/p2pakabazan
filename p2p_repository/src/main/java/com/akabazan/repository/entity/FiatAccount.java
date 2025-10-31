package com.akabazan.repository.entity;

import com.akabazan.framework.data.domain.AuditEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "fiat_accounts")
public class FiatAccount extends AuditEntity {

    @Column(nullable = false)
    private String bankName; // Ví dụ: Vietcombank, Techcombank, MoMo...

    @Column(nullable = false)
    private String accountNumber; // Số tài khoản hoặc số điện thoại ví

    @Column(nullable = false)
    private String accountHolder; // Tên chủ tài khoản

    @Column
    private String branch; // Chi nhánh (optional, với bank)

    @Column(nullable = false)
    private String paymentType; // BANK, MOMO, PAYPAL...

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Getters & Setters
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getAccountHolder() { return accountHolder; }
    public void setAccountHolder(String accountHolder) { this.accountHolder = accountHolder; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
