package com.akabazan.service;

import com.akabazan.service.command.TradeCreateCommand;
import com.akabazan.service.dto.TradeInfoResult;
import com.akabazan.service.dto.TradeResult;

import java.util.List;
import java.util.UUID;

public interface TradeService {

    TradeResult createTrade(TradeCreateCommand command);

    TradeResult confirmPayment(UUID tradeId);

    TradeResult confirmReceived(UUID tradeId);

    TradeResult cancelTrade(UUID tradeId);

    TradeResult cancelTradeByCode(String tradeCode);

    List<TradeResult> getTradesByOrder(UUID orderId);

    List<TradeResult> getTradesByUser(UUID userId);
    
    TradeInfoResult getTradeInfo(UUID tradeId);

    int autoCancelExpiredTrades();
}
