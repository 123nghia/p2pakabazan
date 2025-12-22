package com.akabazan.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("P2P Trading System API")
                        .version("1.0")
                        .description("Interactive documentation for the P2P Trading System endpoints.")
                        .contact(new Contact().name("Akabazan Team").email("support@akabazan.com")))
                .servers(List.of(new Server().url("/").description("Default API base path")));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .packagesToScan("com.akabazan.api.controller")
                .build();
    }
}
