package com.akabazan.service;

import com.akabazan.service.dto.TradeChatResult;

import java.util.List;

public interface TradeChatService {
    TradeChatResult sendMessage(Long tradeId, String message);
    List<TradeChatResult> getMessages(Long tradeId);
}
