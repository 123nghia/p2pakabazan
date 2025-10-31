package com.akabazan.service;

import com.akabazan.service.dto.UserTradesOrdersResult;
import java.util.UUID;

public interface UserTradeOrderService {
    /** Returns combined trades and orders for a user. */
    UserTradesOrdersResult getUserTradesAndOrders(UUID userId);
}
