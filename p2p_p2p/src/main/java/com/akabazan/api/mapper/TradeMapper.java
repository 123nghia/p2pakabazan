package com.akabazan.api.mapper;

import com.akabazan.api.request.OrderRequest;
import com.akabazan.api.request.TradeRequest;
import com.akabazan.service.dto.TradeResult;

public final class TradeMapper {

    private TradeMapper() {
    }

    // Convert từ API request → Result để service xử lý
    public static TradeResult toResult(TradeRequest request) {
        TradeResult tradeResult = new TradeResult();
        tradeResult.setOrderId(request.getOrderId());
        tradeResult.setAmount(request.getAmount());

        return tradeResult;
    }
}
