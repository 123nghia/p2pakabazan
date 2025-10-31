package com.akabazan.service;

import com.akabazan.repository.entity.AdminUser;
import java.util.Optional;
import java.util.UUID;

public interface CurrentAdminService {
    Optional<AdminUser> getCurrentAdmin();
    Optional<UUID> getCurrentAdminId();
    boolean isAuthenticated();
}
