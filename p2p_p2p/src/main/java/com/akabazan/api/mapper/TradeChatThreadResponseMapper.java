package com.akabazan.api.mapper;

import com.akabazan.api.reponse.TradeChatResponse;
import com.akabazan.api.reponse.TradeChatThreadResponse;
import com.akabazan.api.reponse.TradeResponse;
import com.akabazan.service.dto.TradeChatThreadResult;
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
        TradeResponse trade = TradeResponseMapper.from(result.getTrade());
        TradeChatResponse lastMessage = TradeChatResponseMapper.from(result.getLastMessage());
        response.setTrade(trade);
        response.setLastMessage(lastMessage);
        return response;
    }

    public static List<TradeChatThreadResponse> fromList(List<TradeChatThreadResult> results) {
        return results.stream()
                .map(TradeChatThreadResponseMapper::from)
                .collect(Collectors.toList());
    }
}
