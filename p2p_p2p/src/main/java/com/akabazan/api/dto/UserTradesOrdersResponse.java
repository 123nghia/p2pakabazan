package com.akabazan.api.dto;

import java.util.List;

public class UserTradesOrdersResponse {
    private List<OrderResponse> orders;
    private List<TradeResponse> trades;

    public UserTradesOrdersResponse() {
    }

    public UserTradesOrdersResponse(List<OrderResponse> orders, List<TradeResponse> trades) {
        this.orders = orders;
        this.trades = trades;
    }

    public List<OrderResponse> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderResponse> orders) {
        this.orders = orders;
    }

    public List<TradeResponse> getTrades() {
        return trades;
    }

    public void setTrades(List<TradeResponse> trades) {
        this.trades = trades;
    }
}
