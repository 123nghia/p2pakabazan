package com.akabazan.service.impl;

import com.akabazan.common.constant.ErrorCode;
import com.akabazan.common.exception.ApplicationException;
import com.akabazan.repository.OrderRepository;
import com.akabazan.repository.TradeRepository;
import com.akabazan.repository.constant.OrderStatus;
import com.akabazan.repository.constant.TradeStatus;
import com.akabazan.repository.entity.Order;
import com.akabazan.repository.entity.Trade;
import com.akabazan.repository.entity.User;
import com.akabazan.repository.projection.OrderTradeStatsProjection;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
    private final TradeRepository tradeRepository;

    public OrderServiceImpl(CreateOrderUseCase createOrderUseCase,
                            CancelOrderUseCase cancelOrderUseCase,
                            CloseOrderUseCase closeOrderUseCase,
                            ExpireOrdersUseCase expireOrdersUseCase,
                            GetOrdersQuery getOrdersQuery,
                            CurrentUserService  currentUserService,
                            OrderRepository orderRepository,
                            TradeRepository tradeRepository
                            ) {
        this.createOrderUseCase = createOrderUseCase;
        this.cancelOrderUseCase = cancelOrderUseCase;
        this.closeOrderUseCase = closeOrderUseCase;
        this.expireOrdersUseCase = expireOrdersUseCase;
        this.getOrdersQuery = getOrdersQuery;
        this.currentUserService = currentUserService;
        this.orderRepository = orderRepository;
        this.tradeRepository = tradeRepository;
    }

    @Override
    public OrderResult createOrder(OrderCreateCommand command) {

     
        return createOrderUseCase.create(command);
    }

    @Override
    public Page<OrderResult> getOrders(String type,
                                       String token,
                                       List<String> paymentMethods,
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
                paymentMethods,
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
        List<Long> orderIds = orders.stream()
                .map(Order::getId)
                .filter(Objects::nonNull)
                .toList();
        Map<Long, OrderStats> statsByOrder = loadOrderStats(orderIds);
        Map<Long, List<Trade>> tradesByOrder = loadTradesByOrder(orderIds);

        return orders.stream()
                .map(order -> {
                    List<Trade> trades = tradesByOrder.getOrDefault(order.getId(), Collections.emptyList());
                    OrderResult dto = OrderMapper.toResult(order, trades);
                    boolean isOpen = OrderStatus.OPEN.name().equals(order.getStatus());
                    boolean hasActiveTrades = tradeRepository.countByOrderIdAndStatusNotIn(
                            order.getId(),
                            List.of(TradeStatus.CANCELLED, TradeStatus.COMPLETED)) > 0;
                    dto.setCanCancel(isOpen && !hasActiveTrades);
                    OrderStats orderStats = statsByOrder.get(order.getId());
                    long totalTrades = orderStats != null ? orderStats.totalTrades() : 0L;
                    long completedTrades = orderStats != null ? orderStats.completedTrades() : 0L;
                    double completionRate = orderStats != null ? orderStats.completionRate() : 0.0;
                    dto.setTradeCount(totalTrades);
                    dto.setCompletedTradeCount(completedTrades);
                    dto.setCompletionRate(completionRate);
                    return dto;
                })
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

    private Map<Long, OrderStats> loadOrderStats(List<Long> orderIds) {
        if (orderIds == null || orderIds.isEmpty()) {
            return Map.of();
        }
        List<OrderTradeStatsProjection> projections =
                tradeRepository.findTradeStatsByOrderIds(orderIds, TradeStatus.COMPLETED);

        Map<Long, OrderStats> stats = new HashMap<>();
        for (OrderTradeStatsProjection projection : projections) {
            long total = projection.getTotalTrades() != null ? projection.getTotalTrades() : 0L;
            long completed = projection.getCompletedTrades() != null ? projection.getCompletedTrades() : 0L;
            stats.put(projection.getOrderId(), new OrderStats(total, completed));
        }
        return stats;
    }

    private Map<Long, List<Trade>> loadTradesByOrder(List<Long> orderIds) {
        if (orderIds == null || orderIds.isEmpty()) {
            return Map.of();
        }
        return tradeRepository.findByOrderIds(orderIds).stream()
                .collect(Collectors.groupingBy(trade -> trade.getOrder().getId()));
    }

    private record OrderStats(long totalTrades, long completedTrades) {
        double completionRate() {
            return totalTrades == 0 ? 0.0 : (completedTrades * 100.0) / totalTrades;
        }
    }
}
