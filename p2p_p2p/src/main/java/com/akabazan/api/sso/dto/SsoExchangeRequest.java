package com.akabazan.api.sso.dto;

import jakarta.validation.constraints.NotBlank;

public class SsoExchangeRequest {

    @NotBlank
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

