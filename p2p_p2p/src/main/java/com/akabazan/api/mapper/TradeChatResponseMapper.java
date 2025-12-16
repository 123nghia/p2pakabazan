package com.akabazan.api.mapper;

import com.akabazan.api.reponse.TradeChatResponse;
import com.akabazan.service.dto.TradeChatResult;
import java.util.List;
import java.util.stream.Collectors;

public final class TradeChatResponseMapper {

    private TradeChatResponseMapper() {
    }

    public static TradeChatResponse from(TradeChatResult result) {
        if (result == null) {
            return null;
        }
        TradeChatResponse response = new TradeChatResponse();
        response.setId(result.getId());
        response.setTradeId(result.getTradeId());
        response.setSenderId(result.getSenderId());
        response.setMessage(result.getMessage());
        response.setImage(result.getImage());
        response.setTimestamp(result.getTimestamp());
        response.setRecipientRole(result.getRecipientRole());
        return response;
    }

    public static List<TradeChatResponse> fromList(List<TradeChatResult> results) {
        return results.stream()
                .map(TradeChatResponseMapper::from)
                .collect(Collectors.toList());
    }
}
