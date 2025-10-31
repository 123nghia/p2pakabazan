package com.akabazan.service.dto;

import java.util.UUID;


public class AuthResult {

    private final UUID userId;
    private final String email;
    private final String token;

    public AuthResult(UUID userId, String email, String token) {
        this.userId = userId;
        this.email = email;
        this.token = token;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }
}
