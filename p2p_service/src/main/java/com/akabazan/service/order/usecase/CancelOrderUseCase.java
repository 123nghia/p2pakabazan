package com.akabazan.service.order.usecase;

import java.util.UUID;

public interface CancelOrderUseCase {

    void cancel(UUID orderId);
}
