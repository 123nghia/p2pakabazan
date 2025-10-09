package com.akabazan.service.dto;

public class TradeChatThreadResult {

    private TradeResult trade;
    private TradeChatResult lastMessage;
    private String counterpartyName;

    public TradeResult getTrade() {
        return trade;
    }

    public void setTrade(TradeResult trade) {
        this.trade = trade;
    }

    public TradeChatResult getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(TradeChatResult lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getCounterpartyName() {
        return counterpartyName;
    }

    public void setCounterpartyName(String counterpartyName) {
        this.counterpartyName = counterpartyName;
    }
}
