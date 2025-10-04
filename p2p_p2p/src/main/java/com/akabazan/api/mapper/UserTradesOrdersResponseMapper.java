package com.akabazan.api.mapper;

import com.akabazan.api.reponse.OrderResponse;
import com.akabazan.api.reponse.TradeResponse;
import com.akabazan.api.reponse.UserTradesOrdersResponse;
import com.akabazan.service.dto.UserTradesOrdersResult;
import java.util.List;
import java.util.stream.Collectors;

public final class UserTradesOrdersResponseMapper {

    private UserTradesOrdersResponseMapper() {
    }

    public static UserTradesOrdersResponse from(UserTradesOrdersResult result) {
        if (result == null) {
            return null;
        }
        List<OrderResponse> orders = result.getOrders().stream()
                .map(OrderResponseMapper::from)
                .collect(Collectors.toList());
        List<TradeResponse> trades = result.getTrades().stream()
                .map(TradeResponseMapper::from)
                .collect(Collectors.toList());
        return new UserTradesOrdersResponse(orders, trades);
    }
}
