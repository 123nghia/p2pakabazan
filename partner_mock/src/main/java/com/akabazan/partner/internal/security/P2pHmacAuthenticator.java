package com.akabazan.partner.internal.security;

import com.akabazan.partner.config.P2pClientProperties;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Component;

@Component
public class P2pHmacAuthenticator {

    public static final String HEADER_P2P_ID = "X-P2P-Id";
    public static final String HEADER_TIMESTAMP = "X-Timestamp";
    public static final String HEADER_NONCE = "X-Nonce";
    public static final String HEADER_SIGNATURE = "X-Signature";

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final P2pClientProperties clientProperties;
    private final Map<String, Long> nonceExpirationsEpochSeconds = new ConcurrentHashMap<>();

    public P2pHmacAuthenticator(P2pClientProperties clientProperties) {
        this.clientProperties = clientProperties;
    }

    public P2pAuthResult authenticate(HttpServletRequest request, String rawBody) {
        cleanupExpiredNonces();

        String p2pId = readHeaderOrNull(request, HEADER_P2P_ID);
        String timestampRaw = readHeaderOrNull(request, HEADER_TIMESTAMP);
        String nonce = readHeaderOrNull(request, HEADER_NONCE);
        String providedSignature = readHeaderOrNull(request, HEADER_SIGNATURE);

        if (p2pId == null) {
            return P2pAuthResult.unauthorized("Missing header: " + HEADER_P2P_ID);
        }
        if (timestampRaw == null) {
            return P2pAuthResult.unauthorized("Missing header: " + HEADER_TIMESTAMP);
        }
        if (nonce == null) {
            return P2pAuthResult.unauthorized("Missing header: " + HEADER_NONCE);
        }
        if (providedSignature == null) {
            return P2pAuthResult.unauthorized("Missing header: " + HEADER_SIGNATURE);
        }

        String expectedClientId = trimToNull(clientProperties.getId());
        String secret = trimToNull(clientProperties.getSharedSecret());
        if (expectedClientId == null || secret == null) {
            return P2pAuthResult.unauthorized("P2P client is not configured");
        }
        if (!expectedClientId.equals(p2pId)) {
            return P2pAuthResult.unauthorized("Unknown client");
        }

        long nowEpochSeconds = Instant.now().getEpochSecond();
        long timestampSeconds;
        try {
            timestampSeconds = Long.parseLong(timestampRaw);
        } catch (NumberFormatException e) {
            return P2pAuthResult.unauthorized("Invalid timestamp");
        }

        long allowedSkew = Math.max(0, clientProperties.getTimestampSkewSeconds());
        if (Math.abs(nowEpochSeconds - timestampSeconds) > allowedSkew) {
            return P2pAuthResult.unauthorized("Request expired");
        }

        // Replay protection
        long nonceTtlSeconds = Math.max(0, clientProperties.getNonceTtlSeconds());
        if (nonceTtlSeconds <= 0) {
            return P2pAuthResult.unauthorized("Replay protection disabled");
        }
        String nonceKey = p2pId + ":" + nonce;
        long expiresAt = nowEpochSeconds + nonceTtlSeconds;
        Long existing = nonceExpirationsEpochSeconds.putIfAbsent(nonceKey, expiresAt);
        if (existing != null && existing > nowEpochSeconds) {
            return P2pAuthResult.unauthorized("Replay detected");
        }
        nonceExpirationsEpochSeconds.put(nonceKey, expiresAt);

        String method = request.getMethod() == null ? "" : request.getMethod().toUpperCase(Locale.ROOT);
        String path = request.getRequestURI();

        byte[] bodyBytes = rawBody == null ? new byte[0] : rawBody.getBytes(StandardCharsets.UTF_8);
        String bodySha256Hex = sha256Hex(bodyBytes);

        String canonical = method + "\n" + path + "\n" + timestampRaw + "\n" + nonce + "\n" + bodySha256Hex;
        String expectedSignature = hmacBase64(secret, canonical);

        if (!constantTimeEquals(expectedSignature, providedSignature)) {
            return P2pAuthResult.unauthorized("Invalid signature");
        }

        return P2pAuthResult.ok(p2pId);
    }

    private void cleanupExpiredNonces() {
        long now = Instant.now().getEpochSecond();
        nonceExpirationsEpochSeconds.entrySet().removeIf(entry -> entry.getValue() == null || entry.getValue() <= now);
    }

    private static String readHeaderOrNull(HttpServletRequest request, String name) {
        String value = request.getHeader(name);
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static String sha256Hex(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(bytes);
            StringBuilder sb = new StringBuilder(hashed.length * 2);
            for (byte b : hashed) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to compute SHA-256", e);
        }
    }

    private static String hmacBase64(String secret, String canonicalString) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            byte[] signature = mac.doFinal(canonicalString.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signature);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to compute HMAC", e);
        }
    }

    private static boolean constantTimeEquals(String expected, String actual) {
        if (actual == null) {
            return false;
        }
        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                actual.trim().getBytes(StandardCharsets.UTF_8));
    }
}

