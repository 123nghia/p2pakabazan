package com.akabazan.api.mapper;

import com.akabazan.api.reponse.TradeCreatedResponse;
import com.akabazan.service.dto.TradeResult;

public final class TradeCreatedResponseMapper {

    private TradeCreatedResponseMapper() {}

    public static TradeCreatedResponse from(TradeResult result) {
        if (result == null) {
            return null;
        }
        TradeCreatedResponse r = new TradeCreatedResponse();
        r.setId(result.getId());
        r.setOrderId(result.getOrderId());
        r.setAmount(result.getAmount());
        r.setPrice(result.getPrice());
        r.setStatus(result.getStatus());
        r.setTradeCode(result.getTradeCode());
        return r;
    }
}


