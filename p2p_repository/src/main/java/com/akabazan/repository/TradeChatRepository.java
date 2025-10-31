package com.akabazan.repository;

import com.akabazan.repository.entity.TradeChat;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TradeChatRepository extends JpaRepository<TradeChat, UUID> {
    List<TradeChat> findByTradeIdOrderByTimestampAsc(UUID tradeId);

    @Query("""
            SELECT tc FROM TradeChat tc
            WHERE tc.trade.id IN :tradeIds
              AND tc.timestamp = (
                SELECT MAX(innerTc.timestamp)
                FROM TradeChat innerTc
                WHERE innerTc.trade.id = tc.trade.id
              )
            """)
    List<TradeChat> findLatestMessagesByTradeIds(@Param("tradeIds") Collection<UUID> tradeIds);
}
