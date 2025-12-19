package com.akabazan.api.sso.dto;

public class SsoIssueResponse {

    private final String code;
    private final long expiresIn;

    public SsoIssueResponse(String code, long expiresIn) {
        this.code = code;
        this.expiresIn = expiresIn;
    }

    public String getCode() {
        return code;
    }

    public long getExpiresIn() {
        return expiresIn;
    }
}

