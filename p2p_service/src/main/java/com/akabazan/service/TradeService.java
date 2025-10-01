package com.akabazan.service;

import com.akabazan.service.dto.TradeResult;

import java.util.List;

public interface TradeService {

    TradeResult createTrade(TradeResult tradeResult);

    TradeResult confirmPayment(Long tradeId);

    TradeResult confirmReceived(Long tradeId);

    TradeResult cancelTrade(Long tradeId);

    List<TradeResult> getTradesByOrder(Long orderId);
}
