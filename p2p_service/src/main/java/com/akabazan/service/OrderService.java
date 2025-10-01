package com.akabazan.service;
import com.akabazan.service.command.OrderCreateCommand;
import com.akabazan.service.dto.OrderResult;


import java.util.List;

public interface OrderService {

    /** Tạo một order mới */
    OrderResult createOrder(OrderCreateCommand command);

    /** Lấy danh sách order theo filter */
    List<OrderResult> getOrders(String type, String token, String paymentMethod, String sortByPrice);

    void cancelOrder(Long orderId);

    void closeOrder(Long orderId);

    void expireOrders();
}
