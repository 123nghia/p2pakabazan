package com.akabazan.repository;

import com.akabazan.repository.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE " +
           "(:status IS NULL OR o.status = :status) AND " +
           "(:type IS NULL OR o.type = :type) AND " +
           "(:token IS NULL OR o.token = :token) AND " +
           "(:paymentMethod IS NULL OR o.paymentMethod = :paymentMethod)")
    Page<Order> findByStatusAndTypeAndTokenAndPaymentMethod(
            @Param("status") String status,
            @Param("type") String type,
            @Param("token") String token,
            @Param("paymentMethod") String paymentMethod,
            Pageable pageable);
       
            List<Order> findAllByStatusAndExpireAtBefore(String status, LocalDateTime time);
              List<Order> findByUserId(Long userId);


            
}
