package com.akabazan.service;

import com.akabazan.service.command.OrderCreateCommand;
import com.akabazan.service.dto.OrderResult;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;

public interface OrderService {

    /** Tạo một order mới */
    OrderResult createOrder(OrderCreateCommand command);

    /** Lấy danh sách order theo filter */
    Page<OrderResult> getOrders(String type,
                                String token,
                                List<String> paymentMethods,
                                String sortByPrice,
                                String fiat,
                                UUID excludeUserId,
                                int page,
                                int size);

    List<OrderResult> getOrdersByUserToken(String token, String status , String type);

    void cancelOrder(UUID orderId);

    void closeOrder(UUID orderId);

    void expireOrders();
}
