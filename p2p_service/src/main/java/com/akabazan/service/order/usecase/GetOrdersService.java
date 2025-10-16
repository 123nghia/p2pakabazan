package com.akabazan.service.order.usecase;

import com.akabazan.repository.OrderRepository;
import com.akabazan.repository.TradeRepository;
import com.akabazan.repository.constant.OrderStatus;
import com.akabazan.repository.constant.TradeStatus;
import com.akabazan.repository.entity.Order;
import com.akabazan.repository.entity.User;
import com.akabazan.repository.projection.OrderTradeStatsProjection;
import com.akabazan.service.dto.OrderMapper;
import com.akabazan.service.dto.OrderResult;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class GetOrdersService implements GetOrdersQuery {

    private final OrderRepository orderRepository;
    private final TradeRepository tradeRepository;

    public GetOrdersService(OrderRepository orderRepository,
                            TradeRepository tradeRepository) {
        this.orderRepository = orderRepository;
        this.tradeRepository = tradeRepository;
    }

    @Override
    public Page<OrderResult> get(String type,
                                 String token,
                                 List<String> paymentMethods,
                                 String sortByPrice,
                                 String fiat,
                                 int page,
                                 int size) {
        Pageable pageable = buildPageable(sortByPrice, page, size);

        boolean paymentFilterEnabled = paymentMethods != null && !paymentMethods.isEmpty();
        List<String> normalizedPaymentMethods = paymentFilterEnabled
                ? paymentMethods
                : List.of("__ALL__");

        String normalizedType = type != null ? type.toUpperCase() : null;
        String normalizedToken = token != null ? token.toUpperCase() : null;
        String normalizedFiat = fiat != null ? fiat.toUpperCase() : null;

        Page<Order> orders = orderRepository.searchOrders(
                OrderStatus.OPEN.name(),
                normalizedType,
                normalizedToken,
                paymentFilterEnabled,
                normalizedPaymentMethods,
                normalizedFiat,
                pageable
        );
        List<Order> orderContent = orders.getContent();
        Map<Long, TradeStats> statsByUser = buildTradeStats(orderContent);
        Map<Long, OrderStats> statsByOrder = buildOrderStats(orderContent);
        return orders.map(order -> enrich(OrderMapper.toResult(order), statsByUser, statsByOrder));
    }

    private Pageable buildPageable(String sortByPrice, int page, int size) {
        Sort sort = Sort.unsorted();
        if ("asc".equalsIgnoreCase(sortByPrice)) {
            sort = Sort.by(Sort.Direction.ASC, "price");
        } else if ("desc".equalsIgnoreCase(sortByPrice)) {
            sort = Sort.by(Sort.Direction.DESC, "price");
        }
        int resolvedPage = Math.max(page, 0);
        int resolvedSize = size > 0 ? size : 10;
        return sort.isSorted()
                ? PageRequest.of(resolvedPage, resolvedSize, sort)
                : PageRequest.of(resolvedPage, resolvedSize);
    }

    private Map<Long, TradeStats> buildTradeStats(List<Order> orders) {
        Set<Long> userIds = orders.stream()
                .map(Order::getUser)
                .filter(Objects::nonNull)
                .map(User::getId)
                .collect(Collectors.toSet());
        Map<Long, TradeStats> stats = new HashMap<>();
        for (Long userId : userIds) {
            long total = tradeRepository.countByUserId(userId);
            long completed = tradeRepository.countByUserIdAndStatus(userId, TradeStatus.COMPLETED);
            stats.put(userId, new TradeStats(total, completed));
        }
        return stats;
    }

    private Map<Long, OrderStats> buildOrderStats(List<Order> orders) {
        List<Long> orderIds = orders.stream()
                .map(Order::getId)
                .filter(Objects::nonNull)
                .toList();
        if (orderIds.isEmpty()) {
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

    private OrderResult enrich(OrderResult result,
                               Map<Long, TradeStats> statsByUser,
                               Map<Long, OrderStats> statsByOrder) {
        if (result == null) {
            return null;
        }

        OrderStats orderStats = result.getId() != null ? statsByOrder.get(result.getId()) : null;
        if (orderStats != null && orderStats.totalTrades() > 0) {
            result.setTradeCount(orderStats.totalTrades());
            result.setCompletedTradeCount(orderStats.completedTrades());
            result.setCompletionRate(orderStats.completionRate());
            return result;
        }

        Long userId = result.getUserId();
        if (userId != null) {
            TradeStats stat = statsByUser.get(userId);
            if (stat != null) {
                result.setTradeCount(stat.totalTrades());
                result.setCompletedTradeCount(stat.completedTrades());
                result.setCompletionRate(stat.completionRate());
                return result;
            }
        }

        if (orderStats != null) {
            result.setTradeCount(orderStats.totalTrades());
            result.setCompletedTradeCount(orderStats.completedTrades());
            result.setCompletionRate(orderStats.completionRate());
        }

        return result;
    }

    private record TradeStats(long totalTrades, long completedTrades) {
        double completionRate() {
            return totalTrades == 0 ? 0.0 : (completedTrades * 100.0) / totalTrades;
        }
    }

    private record OrderStats(long totalTrades, long completedTrades) {
        double completionRate() {
            return totalTrades == 0 ? 0.0 : (completedTrades * 100.0) / totalTrades;
        }
    }
}
