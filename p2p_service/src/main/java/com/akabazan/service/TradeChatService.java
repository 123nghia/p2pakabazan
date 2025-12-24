package com.akabazan.service;

import com.akabazan.service.dto.TradeChatResult;
import com.akabazan.service.dto.TradeChatThreadResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TradeChatService {
    TradeChatResult sendMessage(UUID tradeId, String message, String image);

    List<TradeChatResult> getMessages(UUID tradeId);

    List<TradeChatResult> getMessages(UUID tradeId, LocalDateTime since);

    List<TradeChatThreadResult> getChatThreadsForCurrentUser();
    void markRead(UUID tradeId);
}
