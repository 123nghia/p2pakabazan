package com.akabazan.service;

import com.akabazan.repository.entity.AdminUser;
import java.util.Optional;

public interface CurrentAdminService {
    Optional<AdminUser> getCurrentAdmin();
    Optional<Long> getCurrentAdminId();
    boolean isAuthenticated();
}


