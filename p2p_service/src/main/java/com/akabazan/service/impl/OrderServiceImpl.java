package com.akabazan.service.impl;

import com.akabazan.service.OrderService;
import com.akabazan.service.command.OrderCreateCommand;
import com.akabazan.service.dto.OrderResult;
import com.akabazan.service.order.usecase.CancelOrderUseCase;
import com.akabazan.service.order.usecase.CloseOrderUseCase;
import com.akabazan.service.order.usecase.CreateOrderUseCase;
import com.akabazan.service.order.usecase.ExpireOrdersUseCase;
import com.akabazan.service.order.usecase.GetOrdersQuery;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    private final CreateOrderUseCase createOrderUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final CloseOrderUseCase closeOrderUseCase;
    private final ExpireOrdersUseCase expireOrdersUseCase;
    private final GetOrdersQuery getOrdersQuery;

    public OrderServiceImpl(CreateOrderUseCase createOrderUseCase,
                            CancelOrderUseCase cancelOrderUseCase,
                            CloseOrderUseCase closeOrderUseCase,
                            ExpireOrdersUseCase expireOrdersUseCase,
                            GetOrdersQuery getOrdersQuery) {
        this.createOrderUseCase = createOrderUseCase;
        this.cancelOrderUseCase = cancelOrderUseCase;
        this.closeOrderUseCase = closeOrderUseCase;
        this.expireOrdersUseCase = expireOrdersUseCase;
        this.getOrdersQuery = getOrdersQuery;
    }

    @Override
    public OrderResult createOrder(OrderCreateCommand command) {
        return createOrderUseCase.create(command);
    }

    @Override
    public Page<OrderResult> getOrders(String type, String token, String paymentMethod, String sortByPrice, int page, int size) {
        return getOrdersQuery.get(type, token, paymentMethod, sortByPrice, page, size);
    }

    @Override
    public void cancelOrder(Long orderId) {
        cancelOrderUseCase.cancel(orderId);
    }

    @Override
    public void closeOrder(Long orderId) {
        closeOrderUseCase.close(orderId);
    }

    @Override
    public void expireOrders() {
        expireOrdersUseCase.expire();
    }
}
