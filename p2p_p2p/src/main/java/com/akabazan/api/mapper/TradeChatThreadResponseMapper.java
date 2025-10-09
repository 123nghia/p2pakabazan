package com.akabazan.api.mapper;

import com.akabazan.api.reponse.TradeChatThreadResponse;
import com.akabazan.service.dto.TradeChatThreadResult;
import com.akabazan.service.dto.TradeResult;
import java.util.List;
import java.util.stream.Collectors;

public final class TradeChatThreadResponseMapper {

    private TradeChatThreadResponseMapper() {
    }

    public static TradeChatThreadResponse from(TradeChatThreadResult result) {
        if (result == null) {
            return null;
        }
        TradeChatThreadResponse response = new TradeChatThreadResponse();
        TradeResult trade = result.getTrade();
        if (trade != null) {
            response.setTradeId(trade.getId());
            response.setTradeCode(trade.getTradeCode());
            response.setAmount(trade.getAmount());
            response.setPrice(trade.getPrice());
            response.setToken(trade.getToken());
            response.setFiat(trade.getFiat());
            response.setStatus(trade.getStatus());
        }
        response.setCounterpartyName(result.getCounterpartyName());
        response.setLastMessage(TradeChatResponseMapper.from(result.getLastMessage()));
        return response;
    }

    public static List<TradeChatThreadResponse> fromList(List<TradeChatThreadResult> results) {
        return results.stream()
                .map(TradeChatThreadResponseMapper::from)
                .collect(Collectors.toList());
    }
}
