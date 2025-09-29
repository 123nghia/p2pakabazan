        package com.akabazan.service.dto;

        import java.time.LocalDateTime;

        public class OrderDTO {

        private Long id;

        private String type; // BUY / SELL
        private String token; // BTC, USDT, ETH
        private Double amount;
        private Double price;
        private Double minLimit;
        private Double maxLimit;
        private String status; // OPEN, CLOSED, CANCELLED
        private String paymentMethod;
        private String priceMode; // MARKET hoặc CUSTOM

        private Double availableAmount;
        private LocalDateTime expireAt;

        // flatten từ fiatAccount và user
        private Long fiatAccountId;
        private Long userId;


        private String bankName;
        private String bankAccount;
        private String accountHolder;

        // getters & setters
        public String getBankName() { return bankName; }
        public void setBankName(String bankName) { this.bankName = bankName; }

        public String getBankAccount() { return bankAccount; }
        public void setBankAccount(String bankAccount) { this.bankAccount = bankAccount; }

        public String getAccountHolder() { return accountHolder; }
        public void setAccountHolder(String accountHolder) { this.accountHolder = accountHolder; }

        // Getter & Setter
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }

        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }

        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }

        public Double getMinLimit() { return minLimit; }
        public void setMinLimit(Double minLimit) { this.minLimit = minLimit; }

        public Double getMaxLimit() { return maxLimit; }
        public void setMaxLimit(Double maxLimit) { this.maxLimit = maxLimit; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

        public String getPriceMode() { return priceMode; }
        public void setPriceMode(String priceMode) { this.priceMode = priceMode; }

        public Double getAvailableAmount() { return availableAmount; }
        public void setAvailableAmount(Double availableAmount) { this.availableAmount = availableAmount; }

        public LocalDateTime getExpireAt() { return expireAt; }
        public void setExpireAt(LocalDateTime expireAt) { this.expireAt = expireAt; }

        public Long getFiatAccountId() { return fiatAccountId; }
        public void setFiatAccountId(Long fiatAccountId) { this.fiatAccountId = fiatAccountId; }

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        }
