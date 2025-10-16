package com.akabazan.repository.projection;

public interface OrderTradeStatsProjection {

    Long getOrderId();

    Long getTotalTrades();

    Long getCompletedTrades();
}

