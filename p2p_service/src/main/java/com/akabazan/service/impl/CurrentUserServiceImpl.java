package com.akabazan.service.impl;

import com.akabazan.repository.UserRepository;
import com.akabazan.repository.entity.User;
import com.akabazan.service.CurrentUserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CurrentUserServiceImpl implements CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> getCurrentUser() {
        
        return getCurrentUserId().flatMap(userRepository::findById);
    }

    @Override
    public Optional<Long> getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(Long.parseLong(auth.getPrincipal().toString()));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean isAuthenticated() {
        return getCurrentUserId().isPresent();
    }
}
