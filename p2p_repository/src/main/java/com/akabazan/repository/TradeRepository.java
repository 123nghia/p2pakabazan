package com.akabazan.repository;

import com.akabazan.repository.constant.TradeStatus;
import com.akabazan.repository.entity.Trade;
import com.akabazan.repository.projection.OrderTradeStatsProjection;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TradeRepository extends JpaRepository<Trade, UUID> {

  List<Trade> findByBuyerId(UUID buyerId);

  List<Trade> findBySellerId(UUID sellerId);

  List<Trade> findByOrderId(UUID orderId);

  boolean existsByOrderId(UUID orderId);

  Optional<Trade> findByTradeCode(String tradeCode);

  @Query("""
      SELECT t FROM Trade t
      WHERE t.buyer.id = :userId OR t.seller.id = :userId
      ORDER BY t.createdAt DESC
      """)
  List<Trade> findByUser(@Param("userId") UUID userId);

  @Query("""
      SELECT t FROM Trade t
      WHERE (
          (:role = 'BUYER' AND t.buyer.id = :userId) OR
          (:role = 'SELLER' AND t.seller.id = :userId) OR
          (:role IS NULL AND (t.buyer.id = :userId OR t.seller.id = :userId))
      )
      AND (:token IS NULL OR t.order.token = :token)
      AND (:fiat IS NULL OR t.order.fiat = :fiat)
      AND (:tradeCode IS NULL OR t.tradeCode LIKE CONCAT('%', :tradeCode, '%'))
      AND (cast(:startDate as timestamp) IS NULL OR t.createdAt >= :startDate)
      AND (cast(:endDate as timestamp) IS NULL OR t.createdAt <= :endDate)
      ORDER BY t.createdAt DESC
      """)
  List<Trade> findByUserAndFilters(
      @Param("userId") UUID userId,
      @Param("token") String token,
      @Param("fiat") String fiat,
      @Param("role") String role,
      @Param("tradeCode") String tradeCode,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  @Query("""
      SELECT t FROM Trade t
      WHERE (t.buyer.id = :userId OR t.seller.id = :userId)
        AND EXISTS (
          SELECT 1 FROM TradeChat chat WHERE chat.trade = t
        )
      """)
  List<Trade> findTradesWithChatsByUser(@Param("userId") UUID userId);

  @Query("""
      SELECT COUNT(t)
      FROM Trade t
      WHERE t.order.id = :orderId
        AND t.status NOT IN (:allowedStatuses)
      """)
  long countByOrderIdAndStatusNotIn(@Param("orderId") UUID orderId,
      @Param("allowedStatuses") Collection<TradeStatus> allowedStatuses);

  @Query("""
      SELECT COUNT(t)
      FROM Trade t
      WHERE t.buyer.id = :userId OR t.seller.id = :userId
      """)
  long countByUserId(@Param("userId") UUID userId);

  @Query("""
      SELECT COUNT(t)
      FROM Trade t
      WHERE (t.buyer.id = :userId OR t.seller.id = :userId)
        AND t.status = :status
      """)
  long countByUserIdAndStatus(@Param("userId") UUID userId, @Param("status") TradeStatus status);

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
  List<Trade> findByOrderIds(@Param("orderIds") Collection<UUID> orderIds);

  @Query("""
      SELECT t.order.id AS orderId,
             COUNT(t) AS totalTrades,
             SUM(CASE WHEN t.status = :completedStatus THEN 1 ELSE 0 END) AS completedTrades
      FROM Trade t
      WHERE t.order.id IN :orderIds
      GROUP BY t.order.id
      """)
  List<OrderTradeStatsProjection> findTradeStatsByOrderIds(@Param("orderIds") Collection<UUID> orderIds,
      @Param("completedStatus") TradeStatus completedStatus);

  @Query("SELECT t.status, COUNT(t) FROM Trade t GROUP BY t.status")
  List<Object[]> countByStatus();

  @Query(value = """
      SELECT CAST(created_at AS DATE) as date, COUNT(*) as count
      FROM trades
      WHERE created_at >= CURRENT_DATE - INTERVAL '7 days'
      GROUP BY CAST(created_at AS DATE)
      ORDER BY date ASC
      """, nativeQuery = true)
  List<Object[]> countDailyTradesLast7Days();
}
