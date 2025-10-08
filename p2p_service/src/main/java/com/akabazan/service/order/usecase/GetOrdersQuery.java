package com.akabazan.service.order.usecase;

import com.akabazan.service.dto.OrderResult;
import org.springframework.data.domain.Page;

public interface GetOrdersQuery {

    Page<OrderResult> get(String type,
                          String token,
                          String paymentMethod,
                          String sortByPrice,
                          String fiat,
                          int page,
                          int size);
}
