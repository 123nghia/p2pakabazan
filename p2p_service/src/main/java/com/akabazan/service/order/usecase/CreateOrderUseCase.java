package com.akabazan.service.order.usecase;

import com.akabazan.service.command.OrderCreateCommand;
import com.akabazan.service.dto.OrderResult;

public interface CreateOrderUseCase {

    OrderResult create(OrderCreateCommand command);
}
