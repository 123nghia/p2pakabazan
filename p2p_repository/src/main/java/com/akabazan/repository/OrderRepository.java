package com.akabazan.repository;

import com.akabazan.repository.entity.Order;
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
    List<Order> findByStatusAndTypeAndTokenAndPaymentMethod(
            @Param("status") String status,
            @Param("type") String type,
            @Param("token") String token,
            @Param("paymentMethod") String paymentMethod);
       
            List<Order> findAllByStatusAndExpireAtBefore(String status, LocalDateTime time);
}