package com.akabazan.api.sso.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Locale;

public final class SsoUserIdentity {

    private static final String DOMAIN = "sso.local";

    private SsoUserIdentity() {}

    public static String toSyntheticEmail(String partnerId, String externalUserId) {
        String normalizedPartnerId = (partnerId == null ? "" : partnerId.trim().toLowerCase(Locale.ROOT));
        String normalizedExternalUserId = (externalUserId == null ? "" : externalUserId.trim());

        String hash = sha256Base64Url(normalizedPartnerId + ":" + normalizedExternalUserId);
        String localPart = normalizedPartnerId.isEmpty() ? hash : normalizedPartnerId + "+" + hash;
        String email = localPart + "@" + DOMAIN;

        if (email.length() <= 100) {
            return email;
        }

        String shortenedPartnerId = normalizedPartnerId.length() > 20 ? normalizedPartnerId.substring(0, 20) : normalizedPartnerId;
        localPart = shortenedPartnerId.isEmpty() ? hash : shortenedPartnerId + "+" + hash;
        email = localPart + "@" + DOMAIN;

        if (email.length() <= 100) {
            return email;
        }

        return hash + "@" + DOMAIN;
    }

    private static String sha256Base64Url(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hashed);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to compute SHA-256", e);
        }
    }
}

