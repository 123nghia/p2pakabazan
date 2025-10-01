package com.akabazan.api.request;

public class OrderRequest {
    private String type;          // BUY / SELL
    private String token;         // BTC, USDT, ETH
    private Double amount;        // Số token
    private Double price;         // Giá 1 token theo fiat
    private String fiat;          // Mã tiền pháp định (VD: VND, USD)
    private String priceMode;     // "MARKET" hoặc "CUSTOM"

    // Fiat Account Info
    private String paymentMethod; // BANK, MOMO, PAYPAL...
    private String bankName;      // Tên ngân hàng hoặc ví (VD: Vietcombank, MoMo)
    private String bankAccount;   // Số tài khoản hoặc số điện thoại ví
    private String accountHolder; // Chủ tài khoản

    // Limit
    private Double minLimit;
    private Double maxLimit;

    // ===== Getters & Setters =====
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getFiat() { return fiat; }
    public void setFiat(String fiat) { this.fiat = fiat; }

    public String getPriceMode() { return priceMode; }
    public void setPriceMode(String priceMode) { this.priceMode = priceMode; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getBankAccount() { return bankAccount; }
    public void setBankAccount(String bankAccount) { this.bankAccount = bankAccount; }

    public String getAccountHolder() { return accountHolder; }
    public void setAccountHolder(String accountHolder) { this.accountHolder = accountHolder; }

    public Double getMinLimit() { return minLimit; }
    public void setMinLimit(Double minLimit) { this.minLimit = minLimit; }

    public Double getMaxLimit() { return maxLimit; }
    public void setMaxLimit(Double maxLimit) { this.maxLimit = maxLimit; }
}
