package com.akabazan.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import java.util.TimeZone;

@SpringBootApplication(scanBasePackages = {
        "com.akabazan.admin",
        "com.akabazan.service",
        "com.akabazan.repository",
        "com.akabazan.common",
        "com.akabazan.notification"
})
@EnableJpaRepositories(basePackages = {
        "com.akabazan.repository",
        "com.akabazan.notification.repository",
        "com.akabazan.admin.security"
})
@EntityScan(basePackages = {
        "com.akabazan.repository.entity",
        "com.akabazan.notification.entity",
        "com.akabazan.admin.security"
})
public class AdminApplication {
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(AdminApplication.class, args);
    }
}


