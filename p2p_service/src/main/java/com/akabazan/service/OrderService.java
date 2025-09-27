package com.akabazan.service;

import com.akabazan.service.dto.OrderDTO;
import com.akabazan.service.dto.TradeDTO;
import com.akabazan.service.dto.ChatMessageDTO;

import java.util.List;

public interface OrderService {

    /** Tạo một order mới */
    OrderDTO createOrder(OrderDTO orderDTO);

    /** Lấy danh sách order theo filter */
    List<OrderDTO> getOrders(String type, String token, String paymentMethod, String sortByPrice);

    void cancelOrder(Long orderId);

    void closeOrder(Long orderId);

    void expireOrders();
}
