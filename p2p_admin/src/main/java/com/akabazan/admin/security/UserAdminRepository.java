package com.akabazan.admin.security;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserAdminRepository extends JpaRepository<UserAdmin, Long> {
    Optional<UserAdmin> findByUsername(String username);
}


