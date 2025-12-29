package com.akabazan.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class AdminSecurityConfig {

        @Bean
        public PasswordEncoder passwordEncoder() {
                // Delegating encoder supports {bcrypt}, {noop}, ... allowing flexible stored
                // hashes
                return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http,
                        UsernameOnlyAuthenticationProvider usernameOnlyAuthProvider) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .authenticationProvider(usernameOnlyAuthProvider)
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/admin-ui/login", "/admin-ui/css/**",
                                                                "/admin-ui/js/**")
                                                .permitAll()
                                                // Restricted management areas for SUPER_ADMIN only
                                                .requestMatchers("/admin-ui/currencies/**", "/admin/currencies/**")
                                                .hasRole("SUPER_ADMIN")
                                                .requestMatchers("/admin-ui/payment-methods/**",
                                                                "/admin/payment-methods/**")
                                                .hasRole("SUPER_ADMIN")
                                                .requestMatchers("/admin-ui/dispute-reasons/**",
                                                                "/admin/dispute-reasons/**")
                                                .hasRole("SUPER_ADMIN")
                                                // General admin access
                                                .requestMatchers("/admin/**").authenticated()
                                                .requestMatchers("/admin-ui/**").authenticated()
                                                .anyRequest().permitAll())
                                .formLogin(form -> form
                                                .loginPage("/admin-ui/login")
                                                .loginProcessingUrl("/admin-ui/login")
                                                .defaultSuccessUrl("/admin-ui", true)
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/admin-ui/logout")
                                                .logoutSuccessUrl("/admin-ui/login?logout")
                                                .permitAll())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

                return http.build();
        }
}
