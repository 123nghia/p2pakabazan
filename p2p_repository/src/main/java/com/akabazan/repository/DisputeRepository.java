package com.akabazan.repository;

import com.akabazan.repository.entity.AdminUser;
import com.akabazan.repository.entity.Dispute;
import com.akabazan.repository.entity.Dispute.DisputeStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DisputeRepository extends JpaRepository<Dispute, UUID> {
    List<Dispute> findByTradeId(UUID tradeId);

    List<Dispute> findByStatusOrderByCreatedAtDesc(DisputeStatus status);

    List<Dispute> findByStatusAndAssignedAdminOrderByCreatedAtDesc(DisputeStatus status, AdminUser assignedAdmin);

    List<Dispute> findByAssignedAdminOrderByCreatedAtDesc(AdminUser assignedAdmin);

    @Query("SELECT d FROM Dispute d JOIN FETCH d.trade t JOIN FETCH t.buyer JOIN FETCH t.seller WHERE d.id = :id")
    Optional<Dispute> findByIdWithTrade(@Param("id") UUID id);
}
