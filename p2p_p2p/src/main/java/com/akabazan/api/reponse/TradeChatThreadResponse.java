package com.akabazan.api.reponse;

public class TradeChatThreadResponse {

    private TradeResponse trade;
    private TradeChatResponse lastMessage;

    public TradeResponse getTrade() {
        return trade;
    }

    public void setTrade(TradeResponse trade) {
        this.trade = trade;
    }

    public TradeChatResponse getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(TradeChatResponse lastMessage) {
        this.lastMessage = lastMessage;
    }
}
