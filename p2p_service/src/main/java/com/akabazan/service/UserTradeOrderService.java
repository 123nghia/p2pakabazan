package com.akabazan.service;
import com.akabazan.service.dto.UserTradesOrdersResult;

public interface UserTradeOrderService {
    /** Tạo một order mới */
    public UserTradesOrdersResult getUserTradesAndOrders(Long userId);
}
