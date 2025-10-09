package com.akabazan.repository;

import com.akabazan.repository.entity.TradeChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Collection;
import java.util.List;

public interface TradeChatRepository extends JpaRepository<TradeChat, Long> {
    List<TradeChat> findByTradeIdOrderByTimestampAsc(Long tradeId);

    @Query("""
            SELECT tc FROM TradeChat tc
            WHERE tc.trade.id IN :tradeIds
              AND tc.timestamp = (
                SELECT MAX(innerTc.timestamp)
                FROM TradeChat innerTc
                WHERE innerTc.trade.id = tc.trade.id
              )
            """)
    List<TradeChat> findLatestMessagesByTradeIds(@Param("tradeIds") Collection<Long> tradeIds);
}
