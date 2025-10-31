package com.akabazan.service;

import com.akabazan.service.dto.TradeChatResult;
import com.akabazan.service.dto.TradeChatThreadResult;

import java.util.List;
import java.util.UUID;

public interface TradeChatService {
    TradeChatResult sendMessage(UUID tradeId, String message);
    List<TradeChatResult> getMessages(UUID tradeId);
    List<TradeChatThreadResult> getChatThreadsForCurrentUser();
}
