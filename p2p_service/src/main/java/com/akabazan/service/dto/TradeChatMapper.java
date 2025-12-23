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
        result.setImage(entity.getImage());
        result.setTimestamp(entity.getTimestamp());
        result.setRecipientRole(entity.getRecipientRole());
        // read flag will be set in service based on user's last read timestamp
        return result;
    }
}
