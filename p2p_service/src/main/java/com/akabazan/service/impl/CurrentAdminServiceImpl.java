package com.akabazan.service.impl;

import com.akabazan.repository.AdminUserRepository;
import com.akabazan.repository.entity.AdminUser;
import com.akabazan.service.CurrentAdminService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CurrentAdminServiceImpl implements CurrentAdminService {

    private final AdminUserRepository adminUserRepository;

    public CurrentAdminServiceImpl(AdminUserRepository adminUserRepository) {
        this.adminUserRepository = adminUserRepository;
    }

    @Override
    public Optional<AdminUser> getCurrentAdmin() {
        return getCurrentAdminId().flatMap(adminUserRepository::findById);
    }

    @Override
    public Optional<UUID> getCurrentAdminId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(UUID.fromString(auth.getPrincipal().toString()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean isAuthenticated() {
        return getCurrentAdminId().isPresent();
    }
}


