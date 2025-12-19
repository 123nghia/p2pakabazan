package com.akabazan.partner.internal.security;

public class P2pAuthResult {

    private final boolean authenticated;
    private final String p2pClientId;
    private final String errorMessage;

    private P2pAuthResult(boolean authenticated, String p2pClientId, String errorMessage) {
        this.authenticated = authenticated;
        this.p2pClientId = p2pClientId;
        this.errorMessage = errorMessage;
    }

    public static P2pAuthResult ok(String p2pClientId) {
        return new P2pAuthResult(true, p2pClientId, null);
    }

    public static P2pAuthResult unauthorized(String message) {
        return new P2pAuthResult(false, null, message);
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public String getP2pClientId() {
        return p2pClientId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}

