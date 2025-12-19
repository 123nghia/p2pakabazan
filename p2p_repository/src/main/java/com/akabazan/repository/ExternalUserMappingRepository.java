package com.akabazan.repository;

import com.akabazan.repository.entity.ExternalUserMapping;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExternalUserMappingRepository extends JpaRepository<ExternalUserMapping, UUID> {

    Optional<ExternalUserMapping> findByPartnerIdAndExternalUserId(String partnerId, String externalUserId);

    Optional<ExternalUserMapping> findByUser_Id(UUID userId);
}

