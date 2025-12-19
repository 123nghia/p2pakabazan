package com.akabazan.repository;

import com.akabazan.repository.entity.DisputeEvidence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface DisputeEvidenceRepository extends JpaRepository<DisputeEvidence, UUID> {
}
