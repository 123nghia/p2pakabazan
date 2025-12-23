package com.akabazan.framework.data.config;

import com.akabazan.framework.data.config.naming.SnakeCasePhysicalNamingStrategy;
import com.akabazan.framework.data.spi.ClockProvider;
import com.akabazan.framework.data.spi.DefaultClockProvider;
import com.akabazan.framework.data.spi.IdGenerator;
import com.akabazan.framework.data.spi.UuidV7Generator;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.auditing.DateTimeProvider;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@AutoConfiguration
@EnableConfigurationProperties(P2PDataProperties.class)
public class DataAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ClockProvider clockProvider(P2PDataProperties props) {
        ZoneId zone = ZoneId.of(props.getTimezone() == null ? "UTC" : props.getTimezone());
        return new DefaultClockProvider(Clock.system(zone));
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "p2p.data.id-strategy", havingValue = "uuid-v7", matchIfMissing = false)
    public IdGenerator idGenerator(P2PDataProperties props, ClockProvider clockProvider) {
        // UUID v7 generator (time-ordered) - only used if explicitly enabled
        return new UuidV7Generator(clockProvider);
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(P2PDataProperties props) {
        return new HibernatePropertiesCustomizer() {
            @Override
            public void customize(Map<String, Object> hibernateProperties) {
                // Enforce JDBC timezone
                String tz = props.getTimezone() == null ? "UTC" : props.getTimezone();
                hibernateProperties.put("hibernate.jdbc.time_zone", tz);
                hibernateProperties.put("hibernate.type.preferred_uuid_jdbc_type", "varchar");

                // Enable snake_case naming strategy if requested
                if (props.isNamingSnakeCase()) {
                    hibernateProperties.put(
                            "hibernate.physical_naming_strategy",
                            SnakeCasePhysicalNamingStrategy.class.getName()
                    );
                }
            }
        };
    }

    @Bean
    @ConditionalOnProperty(name = "p2p.data.naming.snake-case", havingValue = "true")
    @ConditionalOnMissingBean(PhysicalNamingStrategy.class)
    public PhysicalNamingStrategy snakeCasePhysicalNamingStrategy() {
        return new SnakeCasePhysicalNamingStrategy();
    }

    // Integrate with Spring Data JPA auditing time source when auditing is enabled in the app
    @Bean
    @ConditionalOnClass(DateTimeProvider.class)
    public DateTimeProvider dateTimeProvider(ClockProvider clockProvider) {
        return () -> Optional.of(OffsetDateTime.now(clockProvider.getClock()));
    }

    // Note: UuidV7ValueGenerator is not registered as a bean here
    // It will be instantiated by Hibernate via @GenericGenerator and will use
    // BeanFactoryAware to access Spring context
}

