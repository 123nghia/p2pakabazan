package com.akabazan.repository;

import com.akabazan.repository.entity.TradeChat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TradeChatRepository extends JpaRepository<TradeChat, Long> {
    List<TradeChat> findByTradeIdOrderByTimestampAsc(Long tradeId);
}
