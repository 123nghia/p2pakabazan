package com.akabazan.framework.data.config;

import com.akabazan.framework.data.spi.ClockProvider;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

public class DataAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(DataAutoConfiguration.class));

    @Test
    void loadsClockAndHibernateCustomizerWithDefaults() {
        contextRunner.run(ctx -> {
            assertThat(ctx).hasSingleBean(ClockProvider.class);
            assertThat(ctx).hasBean("hibernatePropertiesCustomizer");
        });
    }

    @Test
    void snakeCaseCanBeEnabled() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(DataAutoConfiguration.class))
                .withPropertyValues("p2p.data.naming.snake-case=true")
                .run(ctx -> {
                    assertThat(ctx).hasBean("snakeCasePhysicalNamingStrategy");
                });
    }
}

