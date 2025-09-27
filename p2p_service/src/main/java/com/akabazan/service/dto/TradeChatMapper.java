package com.akabazan.service.dto;

import com.akabazan.repository.entity.TradeChat;

public class TradeChatMapper {

    public static TradeChatDTO toDTO(TradeChat entity) {
        TradeChatDTO dto = new TradeChatDTO();
        dto.setId(entity.getId());
        dto.setTradeId(entity.getTrade().getId());
        dto.setSenderId(entity.getSenderId());
        dto.setMessage(entity.getMessage());
        dto.setTimestamp(entity.getTimestamp());
        return dto;
    }
}
