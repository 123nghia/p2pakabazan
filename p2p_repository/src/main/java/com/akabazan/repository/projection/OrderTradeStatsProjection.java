package com.akabazan.repository.projection;

import java.util.UUID;

public interface OrderTradeStatsProjection {

    UUID getOrderId();

    Long getTotalTrades();

    Long getCompletedTrades();
}

