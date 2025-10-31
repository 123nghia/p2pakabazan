
package com.akabazan.service.config;

import com.akabazan.service.CurrentUserService;
import java.util.UUID;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "securityAuditorAware")
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<UUID> securityAuditorAware(CurrentUserService currentUserService) {
        return currentUserService::getCurrentUserId;
    }
}
