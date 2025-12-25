package com.akabazan.repository;

import com.akabazan.repository.entity.DisputeReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DisputeReasonRepository extends JpaRepository<DisputeReason, UUID> {
    List<DisputeReason> findAllByRole(String role);
}
