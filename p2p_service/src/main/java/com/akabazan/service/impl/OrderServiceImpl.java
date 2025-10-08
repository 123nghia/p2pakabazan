package com.akabazan.service.impl;

import com.akabazan.common.constant.ErrorCode;
import com.akabazan.common.exception.ApplicationException;
import com.akabazan.repository.OrderRepository;
import com.akabazan.repository.entity.Order;
import com.akabazan.repository.entity.User;
import com.akabazan.service.CurrentUserService;
import com.akabazan.service.OrderService;
import com.akabazan.service.command.OrderCreateCommand;
import com.akabazan.service.dto.OrderMapper;
import com.akabazan.service.dto.OrderResult;
import com.akabazan.service.order.usecase.CancelOrderUseCase;
import com.akabazan.service.order.usecase.CloseOrderUseCase;
import com.akabazan.service.order.usecase.CreateOrderUseCase;
import com.akabazan.service.order.usecase.ExpireOrdersUseCase;
import com.akabazan.service.order.usecase.GetOrdersQuery;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    private final CreateOrderUseCase createOrderUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final CloseOrderUseCase closeOrderUseCase;
    private final ExpireOrdersUseCase expireOrdersUseCase;
    private final GetOrdersQuery getOrdersQuery;
    private final CurrentUserService currentUserService;
    private final OrderRepository orderRepository;

    public OrderServiceImpl(CreateOrderUseCase createOrderUseCase,
                            CancelOrderUseCase cancelOrderUseCase,
                            CloseOrderUseCase closeOrderUseCase,
                            ExpireOrdersUseCase expireOrdersUseCase,
                            GetOrdersQuery getOrdersQuery,
                            CurrentUserService  currentUserService,
                            OrderRepository orderRepository
                            ) {
        this.createOrderUseCase = createOrderUseCase;
        this.cancelOrderUseCase = cancelOrderUseCase;
        this.closeOrderUseCase = closeOrderUseCase;
        this.expireOrdersUseCase = expireOrdersUseCase;
        this.getOrdersQuery = getOrdersQuery;
        this.currentUserService = currentUserService;
        this.orderRepository = orderRepository;
    }

    @Override
    public OrderResult createOrder(OrderCreateCommand command) {
        return createOrderUseCase.create(command);
    }

    @Override
    public Page<OrderResult> getOrders(String type,
                                       String token,
                                       String paymentMethod,
                                       String sortByPrice,
                                       String fiat,
                                       int page,
                                       int size) {
        String userAction = type; // hành động người dùng (mua hay bán)
        String oppositeOrderType = "SELL";

        if ("BUY".equalsIgnoreCase(userAction)) {
        oppositeOrderType = "SELL";
        } else if ("SELL".equalsIgnoreCase(userAction)) {
        oppositeOrderType = "BUY";
        }

        return getOrdersQuery.get(oppositeOrderType,
                token,
                paymentMethod,
                sortByPrice,
                fiat,
                page,
                size);
    }

    @Override
    public List<OrderResult> getOrdersByUserToken(String token, String status , String type) {

        User user = currentUserService.getCurrentUser().orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
        Long userId = user.getId();
        List<Order> orders;
        orders = orderRepository.findOrdersByUserAndOptionalFilters(userId, status, type);
        return orders.stream()
                .map(OrderMapper::toResult)
                .toList();
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
