package com.akabazan.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map /image/** URL to local /app/uploads/image/ directory (absolute path in
        // container)
        registry.addResourceHandler("/image/**")
                .addResourceLocations("file:/app/uploads/image/");
    }
}
