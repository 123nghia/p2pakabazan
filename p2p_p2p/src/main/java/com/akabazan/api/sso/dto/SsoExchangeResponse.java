package com.akabazan.api.sso.dto;

import java.util.UUID;

public class SsoExchangeResponse {

    private final String token;
    private final UUID userId;

    public SsoExchangeResponse(String token, UUID userId) {
        this.token = token;
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public UUID getUserId() {
        return userId;
    }
}

