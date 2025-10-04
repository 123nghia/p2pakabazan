package com.akabazan.service;
import com.akabazan.service.command.OrderCreateCommand;
import com.akabazan.service.dto.OrderResult;
import org.springframework.data.domain.Page;

public interface OrderService {

    /** Tạo một order mới */
    OrderResult createOrder(OrderCreateCommand command);

    /** Lấy danh sách order theo filter */
    Page<OrderResult> getOrders(String type, String token, String paymentMethod, String sortByPrice, int page, int size);

    void cancelOrder(Long orderId);

    void closeOrder(Long orderId);

    void expireOrders();
}
