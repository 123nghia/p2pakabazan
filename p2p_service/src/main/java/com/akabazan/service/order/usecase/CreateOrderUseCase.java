package com.akabazan.service.order.usecase;

import com.akabazan.service.dto.OrderDTO;

public interface CreateOrderUseCase {

    OrderDTO create(OrderDTO orderDTO);
}
