package com.akabazan.service.order.usecase;

import com.akabazan.service.dto.OrderDTO;

import java.util.List;

public interface GetOrdersQuery {

    List<OrderDTO> get(String type, String token, String paymentMethod, String sortByPrice);
}
