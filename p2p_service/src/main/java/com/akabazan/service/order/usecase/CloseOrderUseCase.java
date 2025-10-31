package com.akabazan.service.order.usecase;

import java.util.UUID;

public interface CloseOrderUseCase {

    void close(UUID orderId);
}
