package com.akabazan.service.dto;

import com.akabazan.repository.entity.User;

public class IntegrationSyncResult {

    private final User user;
    private final Long walletId;
    private final String walletToken;
    private final String walletAddress;
    private final double walletBalance;
    private final Double walletAvailableBalance;
    private final String token;

    public IntegrationSyncResult(User user,
                                 Long walletId,
                                 String walletToken,
                                 String walletAddress,
                                 double walletBalance,
                                 Double walletAvailableBalance,
                                 String token) {
        this.user = user;
        this.walletId = walletId;
        this.walletToken = walletToken;
        this.walletAddress = walletAddress;
        this.walletBalance = walletBalance;
        this.walletAvailableBalance = walletAvailableBalance;
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public Long getWalletId() {
        return walletId;
    }

    public String getWalletToken() {
        return walletToken;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public double getWalletBalance() {
        return walletBalance;
    }

    public Double getWalletAvailableBalance() {
        return walletAvailableBalance;
    }

    public String getToken() {
        return token;
    }
}
