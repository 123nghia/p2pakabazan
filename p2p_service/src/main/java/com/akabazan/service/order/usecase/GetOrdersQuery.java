package com.akabazan.service.order.usecase;

import com.akabazan.service.dto.OrderResult;
import java.util.List;
import org.springframework.data.domain.Page;

public interface GetOrdersQuery {

    Page<OrderResult> get(String type,
                          String token,
                          List<String> paymentMethods,
                          String sortByPrice,
                          String fiat,
                          int page,
                          int size);
}
