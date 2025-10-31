package com.akabazan.api.reponse;

import java.util.UUID;

public class IntegrationUserResponse {

    private UserResponse user;
    private WalletResponse wallet;
    private String token;

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    public WalletResponse getWallet() {
        return wallet;
    }

    public void setWallet(WalletResponse wallet) {
        this.wallet = wallet;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static class WalletResponse {
        private UUID id;
        private String token;
        private String address;
        private double balance;
        private Double availableBalance;

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

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

        public double getBalance() {
            return balance;
        }

        public void setBalance(double balance) {
            this.balance = balance;
        }

        public Double getAvailableBalance() {
            return availableBalance;
        }

        public void setAvailableBalance(Double availableBalance) {
            this.availableBalance = availableBalance;
        }
    }
}
