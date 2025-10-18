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
        if (auth == null || auth.getName() == null) {
            return Optional.empty();
        }
        return userAdminRepository.findByUsername(auth.getName());
    }
}


