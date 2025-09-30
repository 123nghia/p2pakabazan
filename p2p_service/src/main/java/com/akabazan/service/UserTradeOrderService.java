package com.akabazan.service;
import com.akabazan.service.dto.OrderDTO;
import com.akabazan.service.dto.UserTradesOrdersDTO;

import java.util.List;

public interface UserTradeOrderService {

    /** Tạo một order mới */
    public UserTradesOrdersDTO getUserTradesAndOrders(Long userId);
}
