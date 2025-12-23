package com.akabazan.admin.service;

import com.akabazan.admin.security.UserAdmin;
import com.akabazan.admin.security.UserAdminRepository;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentAdminService {

    private final UserAdminRepository userAdminRepository;

    public CurrentAdminService(UserAdminRepository userAdminRepository) {
        this.userAdminRepository = userAdminRepository;
    }

    public Optional<UserAdmin> getCurrentAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            return Optional.empty();
        }
        Object principal = auth.getPrincipal();

        // Preferred: principal is UUID (set by UsernameOnlyAuthenticationProvider)
        if (principal instanceof java.util.UUID uuid) {
            return userAdminRepository.findById(uuid);
        }

        // Fallback: principal is String -> try to parse as UUID, else treat as username
        if (principal instanceof String str) {
            try {
                java.util.UUID uuid = java.util.UUID.fromString(str);
                return userAdminRepository.findById(uuid);
            } catch (IllegalArgumentException ignored) {
                return userAdminRepository.findByUsername(str);
            }
        }

        return Optional.empty();
    }
}


