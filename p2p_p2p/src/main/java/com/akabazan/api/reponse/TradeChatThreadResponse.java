package com.akabazan.api.reponse;

import java.util.UUID;

public class TradeChatThreadResponse {

    private UUID tradeId;
    private String tradeCode;
    private Double amount;
    private Double price;
    private String token;
    private String fiat;
    private String status;
    private String counterpartyName;
    private TradeChatResponse lastMessage;

    public UUID getTradeId() {
        return tradeId;
    }

    public void setTradeId(UUID tradeId) {
        this.tradeId = tradeId;
    }

    public String getTradeCode() {
        return tradeCode;
    }

    public void setTradeCode(String tradeCode) {
        this.tradeCode = tradeCode;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFiat() {
        return fiat;
    }

    public void setFiat(String fiat) {
        this.fiat = fiat;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCounterpartyName() {
        return counterpartyName;
    }

    public void setCounterpartyName(String counterpartyName) {
        this.counterpartyName = counterpartyName;
    }

    public TradeChatResponse getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(TradeChatResponse lastMessage) {
        this.lastMessage = lastMessage;
    }
}
