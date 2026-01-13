package com.akabazan.service.dto;

import java.util.UUID;

public class AuthResult {

    private final UUID userId;
    private final String email;
    private final String username;
    private final String token;

    public AuthResult(UUID userId, String email, String username, String token) {
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.token = token;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }
}
