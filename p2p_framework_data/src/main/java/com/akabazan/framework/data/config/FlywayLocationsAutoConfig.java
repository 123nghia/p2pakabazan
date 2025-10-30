package com.akabazan.framework.data.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;

import java.util.Arrays;

@AutoConfiguration
@EnableConfigurationProperties(P2PDataProperties.class)
@ConditionalOnClass(Flyway.class)
public class FlywayLocationsAutoConfig {

    @Bean
    @ConditionalOnProperty(prefix = "p2p.data", name = "flyway-auto-locations-enabled", matchIfMissing = true)
    @ConditionalOnMissingBean(FlywayConfigurationCustomizer.class)
    public FlywayConfigurationCustomizer vendorFlywayLocationsCustomizer(Environment env, P2PDataProperties props) {
        // If user explicitly sets spring.flyway.locations, do not override
        String userLocations = env.getProperty("spring.flyway.locations");
        String vendor = props.getVendor() == null ? "postgres" : props.getVendor();
        if (StringUtils.hasText(userLocations)) {
            return configuration -> { /* no-op, user-specified locations */ };
        }
        return configuration -> configuration.locations(
                Arrays.stream(new String[]{
                        "classpath:db/migration",
                        "classpath:db/migration/" + vendor
                }).toArray(String[]::new)
        );
    }
}

