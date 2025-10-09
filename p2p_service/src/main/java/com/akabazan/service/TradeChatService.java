package com.akabazan.service;

import com.akabazan.service.dto.TradeChatResult;
import com.akabazan.service.dto.TradeChatThreadResult;

import java.util.List;

public interface TradeChatService {
    TradeChatResult sendMessage(Long tradeId, String message);
    List<TradeChatResult> getMessages(Long tradeId);
    List<TradeChatThreadResult> getChatThreadsForCurrentUser();
}
