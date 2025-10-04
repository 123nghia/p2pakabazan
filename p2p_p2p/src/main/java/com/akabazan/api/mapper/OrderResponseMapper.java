package com.akabazan.api.mapper;

import com.akabazan.api.reponse.OrderResponse;
import com.akabazan.api.reponse.TradeResponse;
import com.akabazan.service.dto.OrderResult;
import com.akabazan.service.dto.TradeResult;
import java.util.List;
import java.util.stream.Collectors;

public final class OrderResponseMapper {

    private OrderResponseMapper() {
    }

    public static OrderResponse from(OrderResult result) {
        if (result == null) {
            return null;
        }
        OrderResponse response = new OrderResponse();
        response.setId(result.getId());
        response.setType(result.getType());
        response.setToken(result.getToken());
        response.setAmount(result.getAmount());
        response.setPrice(result.getPrice());
        response.setFiat(result.getFiat());
        response.setMinLimit(result.getMinLimit());
        response.setMaxLimit(result.getMaxLimit());
        response.setStatus(result.getStatus());
        response.setPaymentMethod(result.getPaymentMethod());
        response.setPriceMode(result.getPriceMode());
        response.setAvailableAmount(result.getAvailableAmount());
        response.setExpireAt(result.getExpireAt());
        response.setFiatAccountId(result.getFiatAccountId());
        response.setUserId(result.getUserId());
        response.setBankName(result.getBankName());
        response.setBankAccount(result.getBankAccount());
        response.setAccountHolder(result.getAccountHolder());

        List<TradeResult> trades = result.getTrades();
        if (trades != null) {
            List<TradeResponse> tradeResponses = trades.stream()
                    .map(TradeResponseMapper::from)
                    .collect(Collectors.toList());
            response.setTrades(tradeResponses);
        }
        return response;
    }

    public static List<OrderResponse> fromList(List<OrderResult> results) {
        return results.stream()
                .map(OrderResponseMapper::from)
                .collect(Collectors.toList());
    }
}
