package com.akabazan.admin.security;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAdminRepository extends JpaRepository<UserAdmin, UUID> {
    Optional<UserAdmin> findByUsername(String username);
}

