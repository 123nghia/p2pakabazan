package com.akabazan.service.order.usecase;

import com.akabazan.service.dto.OrderResult;

import java.util.List;

public interface GetOrdersQuery {

    List<OrderResult> get(String type, String token, String paymentMethod, String sortByPrice);
}
