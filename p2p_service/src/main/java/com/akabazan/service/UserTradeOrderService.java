package com.akabazan.service;
import com.akabazan.service.dto.OrderResult;
import com.akabazan.service.dto.UserTradesOrdersResult;

import java.util.List;

public interface UserTradeOrderService {

    /** Tạo một order mới */
    public UserTradesOrdersResult getUserTradesAndOrders(Long userId);
}
