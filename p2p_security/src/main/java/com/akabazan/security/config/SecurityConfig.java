package com.akabazan.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import com.akabazan.security.JwtAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
 public SecurityConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint)
                        {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }
    @Bean
    public SecretKey secretKey() {
        // Key phải >= 32 ký tự (256 bits)
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
    
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(SecretKey secretKey) {
        return new JwtAuthenticationFilter(secretKey);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/market/**").permitAll()
                .requestMatchers("/api/integration/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()   // login/register không cần token
                .requestMatchers(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/api/swagger-ui/**",
                        "/api/swagger-ui.html",
                        "/api/v3/api-docs/**").permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)  // 401
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
            http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
            return http.build();
    }
}
