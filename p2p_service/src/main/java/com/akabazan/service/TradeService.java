package com.akabazan.service;

import com.akabazan.service.dto.ChatMessageDTO;
import com.akabazan.service.dto.TradeDTO;

public interface TradeService {

    TradeDTO createTrade(TradeDTO tradeDTO);

    TradeDTO confirmPayment(Long tradeId);

    TradeDTO confirmReceived(Long tradeId);

    TradeDTO sendChatMessage(Long tradeId, ChatMessageDTO messageDTO);

    TradeDTO openDispute(Long tradeId, String reason, String evidence);
}
