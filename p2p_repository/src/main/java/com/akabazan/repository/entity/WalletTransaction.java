package com.akabazan.repository.entity;

import com.akabazan.repository.constant.WalletTransactionType;
import jakarta.persistence.*;

@Entity
@Table(name = "wallet_transactions")
public class WalletTransaction extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 32)
    private WalletTransactionType type;

    @Column(nullable = false)
    private double amount;

    @Column(name = "balance_before", nullable = false)
    private double balanceBefore;

    @Column(name = "balance_after", nullable = false)
    private double balanceAfter;

    @Column(name = "available_before", nullable = false)
    private double availableBefore;

    @Column(name = "available_after", nullable = false)
    private double availableAfter;

    @Column(name = "performed_by")
    private Long performedBy;

    @Column(name = "reference_type")
    private String referenceType;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(length = 255)
    private String description;

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public WalletTransactionType getType() {
        return type;
    }

    public void setType(WalletTransactionType type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getBalanceBefore() {
        return balanceBefore;
    }

    public void setBalanceBefore(double balanceBefore) {
        this.balanceBefore = balanceBefore;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(double balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public double getAvailableBefore() {
        return availableBefore;
    }

    public void setAvailableBefore(double availableBefore) {
        this.availableBefore = availableBefore;
    }

    public double getAvailableAfter() {
        return availableAfter;
    }

    public void setAvailableAfter(double availableAfter) {
        this.availableAfter = availableAfter;
    }

    public Long getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(Long performedBy) {
        this.performedBy = performedBy;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
