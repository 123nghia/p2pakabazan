package com.akabazan.repository;

import com.akabazan.repository.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
  @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
  List<Object[]> countByStatus();

  @Query("SELECT o.type, COUNT(o) FROM Order o GROUP BY o.type")
  List<Object[]> countByType();

  @Query("""
      SELECT o FROM Order o
      WHERE (:status IS NULL OR o.status = :status)
        AND (:type IS NULL OR UPPER(o.type) = :type)
        AND (:token IS NULL OR UPPER(o.token) = :token)
        AND (:paymentFilterEnabled = false OR UPPER(o.paymentMethod) IN (:paymentMethods))
        AND (:fiat IS NULL OR UPPER(o.fiat) = :fiat)
        AND (:excludeUserId IS NULL OR o.user.id <> :excludeUserId)
        AND (o.availableAmount IS NOT NULL AND o.availableAmount > 0)
      """)
  Page<Order> searchOrders(@Param("status") String status,
      @Param("type") String type,
      @Param("token") String token,
      @Param("paymentFilterEnabled") boolean paymentFilterEnabled,
      @Param("paymentMethods") List<String> paymentMethods,
      @Param("fiat") String fiat,
      @Param("excludeUserId") UUID excludeUserId,
      Pageable pageable);

  List<Order> findAllByStatusAndExpireAtBefore(String status, LocalDateTime time);

  List<Order> findByUserId(UUID userId);

  @Query(value = """
      SELECT * FROM orders o
      WHERE o.user_id = :userId
        AND (CAST(:status AS TEXT) IS NULL OR o.status = :status)
        AND (CAST(:type AS TEXT) IS NULL OR o.type = :type)
      ORDER BY o.created_at DESC
      """, nativeQuery = true)
  List<Order> findOrdersByUserAndOptionalFilters(@Param("userId") UUID userId,
      @Param("status") String status,
      @Param("type") String type);

}
