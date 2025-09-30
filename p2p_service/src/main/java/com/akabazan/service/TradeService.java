package com.akabazan.service;

import com.akabazan.service.dto.TradeDTO;

import java.util.List;

public interface TradeService {

    TradeDTO createTrade(TradeDTO tradeDTO);

    TradeDTO confirmPayment(Long tradeId);

    TradeDTO confirmReceived(Long tradeId);

    TradeDTO cancelTrade(Long tradeId);

    List<TradeDTO> getTradesByOrder(Long orderId);
}
