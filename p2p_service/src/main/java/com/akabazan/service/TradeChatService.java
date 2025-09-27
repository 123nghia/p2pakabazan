package com.akabazan.service;

import com.akabazan.service.dto.TradeChatDTO;

import java.util.List;

public interface TradeChatService {
    TradeChatDTO sendMessage(Long tradeId, String message);
    List<TradeChatDTO> getMessages(Long tradeId);
}
