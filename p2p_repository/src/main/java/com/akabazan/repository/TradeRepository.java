package com.akabazan.repository;

import com.akabazan.repository.constant.TradeStatus;
import com.akabazan.repository.entity.Trade;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TradeRepository extends JpaRepository<Trade, Long> {

    List<Trade> findByBuyerId(Long buyerId);

    List<Trade> findBySellerId(Long sellerId);

    List<Trade> findByOrderId(Long orderId);

    boolean existsByOrderId(Long orderId);

    Optional<Trade> findByTradeCode(String tradeCode);

  @Query("""
    SELECT t FROM Trade t
    WHERE t.buyer.id = :userId OR t.seller.id = :userId
    ORDER BY t.createdAt DESC
""")
    List<Trade> findByUser(@Param("userId") Long userId);

    @Query("""
            SELECT COUNT(t)
            FROM Trade t
            WHERE t.order.id = :orderId
              AND t.status NOT IN (:allowedStatuses)
            """)
    long countByOrderIdAndStatusNotIn(@Param("orderId") Long orderId,
                                      @Param("allowedStatuses") Collection<TradeStatus> allowedStatuses);

    @Query("""
            SELECT COUNT(t)
            FROM Trade t
            WHERE t.buyer.id = :userId OR t.seller.id = :userId
            """)
    long countByUserId(@Param("userId") Long userId);

    @Query("""
            SELECT COUNT(t)
            FROM Trade t
            WHERE (t.buyer.id = :userId OR t.seller.id = :userId)
              AND t.status = :status
            """)
    long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") TradeStatus status);
}
