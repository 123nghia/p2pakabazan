package com.akabazan.service.dto;

import java.util.Objects;

public class IntegrationWalletData {

    private String token;
    private String address;
    private Double balance;
    private Double availableBalance;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Double getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(Double availableBalance) {
        this.availableBalance = availableBalance;
    }

    public void requireValid() {
        Objects.requireNonNull(token, "Wallet token must not be null");
        Objects.requireNonNull(balance, "Wallet balance must not be null");
    }
}
