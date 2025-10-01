package com.akabazan.api.mapper;

import com.akabazan.api.dto.TradeResponse;
import com.akabazan.service.dto.TradeResult;

public final class TradeResponseMapper {

    private TradeResponseMapper() {
    }

    public static TradeResponse from(TradeResult result) {
        if (result == null) {
            return null;
        }
        TradeResponse response = new TradeResponse();
        response.setId(result.getId());
        response.setOrderId(result.getOrderId());
        response.setBuyerId(result.getBuyerId());
        response.setSellerId(result.getSellerId());
        response.setAmount(result.getAmount());
        response.setStatus(result.getStatus());
        response.setEscrow(result.isEscrow());
        return response;
    }
}
