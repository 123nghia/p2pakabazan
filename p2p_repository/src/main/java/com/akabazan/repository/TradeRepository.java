package com.akabazan.repository;

import com.akabazan.repository.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TradeRepository extends JpaRepository<Trade, Long> {

    List<Trade> findByBuyerId(Long buyerId);

    List<Trade> findBySellerId(Long sellerId);

    List<Trade> findByOrderId(Long orderId);
}
