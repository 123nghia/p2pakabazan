package com.akabazan.repository;

import com.akabazan.repository.entity.TradeChatRead;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TradeChatReadRepository extends JpaRepository<TradeChatRead, UUID> {

    Optional<TradeChatRead> findByTradeIdAndUserId(UUID tradeId, UUID userId);

    List<TradeChatRead> findByUserIdAndTradeIdIn(UUID userId, Collection<UUID> tradeIds);

    @Query("""
            SELECT r FROM TradeChatRead r
            WHERE r.trade.id = :tradeId AND r.userId = :userId
            """)
    Optional<TradeChatRead> findExisting(@Param("tradeId") UUID tradeId, @Param("userId") UUID userId);
}
