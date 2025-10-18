package com.akabazan.admin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class AdminJwtConfig {

    @Value("${jwt.secret:admin-demo-secret-key-which-is-at-least-32-chars!!}")
    private String jwtSecret;

    @Bean
    public SecretKey secretKey() {
        String secret = ensureMinLength(jwtSecret, 32);
        return new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    private String ensureMinLength(String s, int min) {
        if (s == null) return "admin-demo-secret-key-which-is-at-least-32-chars!!";
        if (s.length() >= min) return s;
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < min) sb.append('0');
        return sb.toString();
    }
}


