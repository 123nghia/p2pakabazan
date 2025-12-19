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

    @Query("""
            SELECT o FROM Order o
            WHERE o.user.id = :userId
              AND (:status IS NULL OR o.status = :status)
              AND (:type IS NULL OR o.type = :type)
            ORDER BY o.createdAt DESC
            """)
    List<Order> findOrdersByUserAndOptionalFilters(UUID userId, String status, String type);


            
}
