package com.akabazan.api.sso.security;

public class PartnerAuthResult {

    private final boolean authenticated;
    private final String partnerId;
    private final String errorMessage;

    private PartnerAuthResult(boolean authenticated, String partnerId, String errorMessage) {
        this.authenticated = authenticated;
        this.partnerId = partnerId;
        this.errorMessage = errorMessage;
    }

    public static PartnerAuthResult ok(String partnerId) {
        return new PartnerAuthResult(true, partnerId, null);
    }

    public static PartnerAuthResult unauthorized(String errorMessage) {
        return new PartnerAuthResult(false, null, errorMessage);
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}

