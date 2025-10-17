package com.akabazan.service.dto;

import com.akabazan.repository.entity.TradeChat;

public class TradeChatMapper {

    private TradeChatMapper() {
    }

    public static TradeChatResult toResult(TradeChat entity) {
        TradeChatResult result = new TradeChatResult();
        result.setId(entity.getId());
        result.setTradeId(entity.getTrade().getId());
        result.setSenderId(entity.getSenderId());
        result.setMessage(entity.getMessage());
        result.setTimestamp(entity.getTimestamp());
        result.setRecipientRole(entity.getRecipientRole());
        return result;
    }
}
