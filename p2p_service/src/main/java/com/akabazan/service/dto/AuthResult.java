package com.akabazan.service.dto;

public class AuthResult {

    private final Long userId;
    private final String email;
    private final String token;

    public AuthResult(Long userId, String email, String token) {
        this.userId = userId;
        this.email = email;
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }
}
