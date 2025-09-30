package com.akabazan.service.dto;

import java.util.List;

public class UserTradesOrdersDTO {
    private List<OrderDTO> orders;
    private List<TradeDTO> trades;
     public UserTradesOrdersDTO(List<OrderDTO> orders, List<TradeDTO> trades) {
        this.orders = orders;
        this.trades = trades;
    }

    public List<OrderDTO> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderDTO> orders) {
        this.orders = orders;
    }

    public List<TradeDTO> getTrades() {
        return trades;
    }

    public void setTrades(List<TradeDTO> trades) {
        this.trades = trades;
    }
}