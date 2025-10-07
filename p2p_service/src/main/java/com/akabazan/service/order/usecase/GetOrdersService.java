package com.akabazan.service.order.usecase;

import com.akabazan.repository.OrderRepository;
import com.akabazan.repository.constant.OrderStatus;
import com.akabazan.repository.entity.Order;
import com.akabazan.service.dto.OrderMapper;
import com.akabazan.service.dto.OrderResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class GetOrdersService implements GetOrdersQuery {

    private final OrderRepository orderRepository;

    public GetOrdersService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Page<OrderResult> get(String type, String token, String paymentMethod, String sortByPrice, int page, int size) {
        Pageable pageable = buildPageable(sortByPrice, page, size);
        Page<Order> orders = orderRepository.findByStatusAndTypeAndTokenAndPaymentMethod(
                OrderStatus.OPEN.name(),
                type != null ? type.toUpperCase() : null,
                token,
                paymentMethod,
                pageable
        );
        return orders.map(OrderMapper::toResult);
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
}
