package com.akabazan.service.order.usecase;

import com.akabazan.repository.OrderRepository;
import com.akabazan.repository.constant.OrderStatus;
import com.akabazan.repository.entity.Order;
import com.akabazan.service.dto.OrderDTO;
import com.akabazan.service.dto.OrderMapper;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetOrdersService implements GetOrdersQuery {

    private final OrderRepository orderRepository;

    public GetOrdersService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public List<OrderDTO> get(String type, String token, String paymentMethod, String sortByPrice) {
        List<Order> orders = orderRepository.findByStatusAndTypeAndTokenAndPaymentMethod(
                OrderStatus.OPEN.name(),
                type != null ? type.toUpperCase() : null,
                token,
                paymentMethod
        );

        applySorting(sortByPrice, orders);

        return orders.stream()
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
    }

    private void applySorting(String sortByPrice, List<Order> orders) {
        if ("asc".equalsIgnoreCase(sortByPrice)) {
            orders.sort(Comparator.comparingDouble(Order::getPrice));
        } else if ("desc".equalsIgnoreCase(sortByPrice)) {
            orders.sort(Comparator.comparingDouble(Order::getPrice).reversed());
        }
    }
}
