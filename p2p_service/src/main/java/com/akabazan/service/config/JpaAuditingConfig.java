package com.akabazan.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.akabazan.service.CurrentUserService;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "securityAuditorAware")
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<Long> securityAuditorAware(CurrentUserService currentUserService) {
        return currentUserService::getCurrentUserId;
    }
}
