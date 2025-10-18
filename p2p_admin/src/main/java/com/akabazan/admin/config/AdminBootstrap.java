package com.akabazan.admin.config;

import com.akabazan.admin.security.UserAdmin;
import com.akabazan.admin.security.UserAdminRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminBootstrap {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);

    @Value("${admin.default.username:admin}")
    private String defaultUsername;

    @Value("${admin.default.password:admin}")
    private String defaultPassword;

    @Bean
    public CommandLineRunner ensureDefaultAdmin(UserAdminRepository repository,
                                                PasswordEncoder passwordEncoder) {
        return args -> {
            repository.findByUsername(defaultUsername).ifPresentOrElse(existing -> {
                // keep enabled, but ensure password is encoded in delegating format
                if (existing.getPasswordHash() == null || existing.getPasswordHash().isBlank()) {
                    existing.setPasswordHash(passwordEncoder.encode(defaultPassword));
                    repository.save(existing);
                    log.info("Seeded admin password for existing user '{}'.", defaultUsername);
                }
            }, () -> {
                UserAdmin admin = new UserAdmin();
                admin.setUsername(defaultUsername);
                admin.setPasswordHash(passwordEncoder.encode(defaultPassword));
                admin.setEnabled(true);
                repository.save(admin);
                log.info("Created default admin user '{}'.", defaultUsername);
            });
        };
    }
}


