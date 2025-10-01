package com.akabazan.api.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class IntegrationUserRequest {

    @NotBlank
    private String userId;

    @NotNull
    @Valid
    private WalletPayload wallet;

    @Pattern(regexp = "(?i)UNVERIFIED|PENDING|VERIFIED|REJECTED", message = "kycStatus must be one of UNVERIFIED, PENDING, VERIFIED, REJECTED")
    private String kycStatus;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public WalletPayload getWallet() {
        return wallet;
    }

    public void setWallet(WalletPayload wallet) {
        this.wallet = wallet;
    }

    public String getKycStatus() {
        return kycStatus;
    }

    public void setKycStatus(String kycStatus) {
        this.kycStatus = kycStatus;
    }

    public static class WalletPayload {

        @NotBlank
        private String token;

        private String address;

        @NotNull
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
    }
}
