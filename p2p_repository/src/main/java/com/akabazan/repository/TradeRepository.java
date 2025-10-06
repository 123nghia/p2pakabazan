package com.akabazan.repository;

import com.akabazan.repository.entity.Trade;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TradeRepository extends JpaRepository<Trade, Long> {

    List<Trade> findByBuyerId(Long buyerId);

    List<Trade> findBySellerId(Long sellerId);

    List<Trade> findByOrderId(Long orderId);

    Optional<Trade> findByTradeCode(String tradeCode);

    @Query("SELECT t FROM Trade t WHERE t.buyer.id = :userId OR t.seller.id = :userId")
    List<Trade> findByUser(@Param("userId") Long userId);
}
