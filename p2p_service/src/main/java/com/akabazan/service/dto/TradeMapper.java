package com.akabazan.service.dto;

import com.akabazan.repository.entity.Trade;

public class TradeMapper {

    public static TradeDTO toDTO(Trade trade) {
        TradeDTO dto = new TradeDTO();
        dto.setId(trade.getId());
        dto.setOrderId(trade.getOrder().getId());
        dto.setBuyerId(trade.getBuyer().getId());
        dto.setSellerId(trade.getSeller().getId());
        dto.setAmount(trade.getAmount());
        dto.setStatus(trade.getStatus().name());
        dto.setEscrow(trade.isEscrow());
        return dto;
    }
}
