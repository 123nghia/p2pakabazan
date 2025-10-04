package com.akabazan.api.reponse;

import com.akabazan.repository.entity.User.KycStatus;
import java.util.List;

public class UserResponse {

    private Long id;
    private String email;
    private String phone;
    private KycStatus kycStatus;
    private List<WalletResponse> wallets;
    private List<LoginHistoryResponse> loginHistory;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public KycStatus getKycStatus() {
        return kycStatus;
    }

    public void setKycStatus(KycStatus kycStatus) {
        this.kycStatus = kycStatus;
    }

    public List<WalletResponse> getWallets() {
        return wallets;
    }

    public void setWallets(List<WalletResponse> wallets) {
        this.wallets = wallets;
    }

    public List<LoginHistoryResponse> getLoginHistory() {
        return loginHistory;
    }

    public void setLoginHistory(List<LoginHistoryResponse> loginHistory) {
        this.loginHistory = loginHistory;
    }

    public static class WalletResponse {
        private String token;
        private String address;
        private double balance;

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
    }

    public static class LoginHistoryResponse {
        private String ip;
        private String device;
        private java.time.LocalDateTime timestamp;

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getDevice() {
            return device;
        }

        public void setDevice(String device) {
            this.device = device;
        }

        public java.time.LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(java.time.LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }
    }
}
