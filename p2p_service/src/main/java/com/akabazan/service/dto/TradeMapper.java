package com.akabazan.service.dto;

import com.akabazan.repository.entity.Trade;

public class TradeMapper {

    private TradeMapper() {
    }

    public static TradeResult toResult(Trade trade) {
        TradeResult result = new TradeResult();
        result.setId(trade.getId());
        result.setOrderId(trade.getOrder().getId());
        result.setBuyerId(trade.getBuyer().getId());
        result.setSellerId(trade.getSeller().getId());
        result.setAmount(trade.getAmount());
        result.setStatus(trade.getStatus().name());
        result.setEscrow(trade.isEscrow());
        return result;
    }
}
