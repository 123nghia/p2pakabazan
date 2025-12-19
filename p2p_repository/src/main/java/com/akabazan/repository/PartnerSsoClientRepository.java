package com.akabazan.repository;

import com.akabazan.repository.entity.PartnerSsoClient;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerSsoClientRepository extends JpaRepository<PartnerSsoClient, UUID> {

    Optional<PartnerSsoClient> findByPartnerId(String partnerId);

    boolean existsByPartnerId(String partnerId);
}

