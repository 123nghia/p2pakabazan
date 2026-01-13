package com.akabazan.admin.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UsernameOnlyAuthenticationProvider implements AuthenticationProvider {

    private final UserAdminRepository repository;

    public UsernameOnlyAuthenticationProvider(UserAdminRepository repository) {
        this.repository = repository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        if (username == null || username.isBlank()) {
            throw new BadCredentialsException("Username is required");
        }
        UserAdmin admin = repository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("User not found"));
        if (!admin.isEnabled()) {
            throw new BadCredentialsException("User disabled");
        }
        // Assign role from the database
        String roleName = admin.getRole() != null ? admin.getRole().name() : "SOFT_ADMIN";
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                admin.getId(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + roleName)));
        // Keep username in details for display purposes if needed
        token.setDetails(admin.getUsername());
        return token;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
