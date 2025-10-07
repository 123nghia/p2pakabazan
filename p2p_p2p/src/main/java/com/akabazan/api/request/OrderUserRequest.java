package com.akabazan.api.request;

public class OrderUserRequest {
    private String type;          // BUY / SELL
    private String token;         // BTC, USDT, ETH
    private String status;
   

    // ===== Getters & Setters =====
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String gettoken() { return token; }
    public void settoken(String token) { this.token = token; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

   
}
