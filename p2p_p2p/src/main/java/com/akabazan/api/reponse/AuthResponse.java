package com.akabazan.api.reponse;

import java.util.UUID;

public class AuthResponse {

    private String token;
    private UUID userId;
    private String email;

    public AuthResponse() {
    }

    public AuthResponse(String token, UUID userId, String email) {
        this.token = token;
        this.userId = userId;
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static AuthResponse from(String token, UUID userId, String email) {
        return new AuthResponse(token, userId, email);
    }
}
