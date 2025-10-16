package com.akabazan.repository;

import com.akabazan.repository.constant.TradeStatus;
import com.akabazan.repository.entity.Trade;
import com.akabazan.repository.projection.OrderTradeStatsProjection;
import java.time.LocalDateTime;
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
            SELECT t FROM Trade t
            WHERE (t.buyer.id = :userId OR t.seller.id = :userId)
              AND EXISTS (
                SELECT 1 FROM TradeChat chat WHERE chat.trade = t
              )
            """)
    List<Trade> findTradesWithChatsByUser(@Param("userId") Long userId);

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

    @Query("""
            SELECT t FROM Trade t
            WHERE t.status = :status
              AND t.createdAt <= :threshold
            """)
    List<Trade> findByStatusAndCreatedAtBefore(@Param("status") TradeStatus status,
                                               @Param("threshold") LocalDateTime threshold);

    @Query("""
            SELECT t FROM Trade t
            WHERE t.order.id IN :orderIds
            ORDER BY t.createdAt DESC
            """)
    List<Trade> findByOrderIds(@Param("orderIds") Collection<Long> orderIds);

    @Query("""
            SELECT t.order.id AS orderId,
                   COUNT(t) AS totalTrades,
                   SUM(CASE WHEN t.status = :completedStatus THEN 1 ELSE 0 END) AS completedTrades
            FROM Trade t
            WHERE t.order.id IN :orderIds
            GROUP BY t.order.id
            """)
    List<OrderTradeStatsProjection> findTradeStatsByOrderIds(@Param("orderIds") Collection<Long> orderIds,
                                                             @Param("completedStatus") TradeStatus completedStatus);
}
