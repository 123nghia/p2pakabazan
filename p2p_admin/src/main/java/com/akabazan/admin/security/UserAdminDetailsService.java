package com.akabazan.admin.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAdminDetailsService implements UserDetailsService {

    private final UserAdminRepository repository;

    public UserAdminDetailsService(UserAdminRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAdmin admin = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found"));
        List<GrantedAuthority> roles = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        return new User(admin.getUsername(), admin.getPasswordHash(), admin.isEnabled(), true, true, true, roles);
    }
}


