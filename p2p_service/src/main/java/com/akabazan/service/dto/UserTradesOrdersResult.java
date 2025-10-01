package com.akabazan.service.dto;

import java.util.List;

public class UserTradesOrdersResult {
    private List<OrderResult> orders;
    private List<TradeResult> trades;

    public UserTradesOrdersResult(List<OrderResult> orders, List<TradeResult> trades) {
        this.orders = orders;
        this.trades = trades;
    }

    public List<OrderResult> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderResult> orders) {
        this.orders = orders;
    }

    public List<TradeResult> getTrades() {
        return trades;
    }

    public void setTrades(List<TradeResult> trades) {
        this.trades = trades;
    }
}
