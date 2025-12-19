package com.akabazan.api.sso.security;

import com.akabazan.api.sso.config.SsoProperties;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class PartnerHmacAuthenticator {

    public static final String HEADER_PARTNER_ID = "X-Partner-Id";
    public static final String HEADER_TIMESTAMP = "X-Timestamp";
    public static final String HEADER_NONCE = "X-Nonce";
    public static final String HEADER_SIGNATURE = "X-Signature";

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final SsoProperties ssoProperties;
    private final StringRedisTemplate stringRedisTemplate;
    private final Clock clock;

    @Autowired
    public PartnerHmacAuthenticator(SsoProperties ssoProperties,
                                   StringRedisTemplate stringRedisTemplate) {
        this(ssoProperties, stringRedisTemplate, Clock.systemUTC());
    }

    PartnerHmacAuthenticator(SsoProperties ssoProperties,
                             StringRedisTemplate stringRedisTemplate,
                             Clock clock) {
        this.ssoProperties = ssoProperties;
        this.stringRedisTemplate = stringRedisTemplate;
        this.clock = clock;
    }

    public PartnerAuthResult authenticate(HttpServletRequest request, String rawBody) {
        String partnerId = readHeaderOrNull(request, HEADER_PARTNER_ID);
        String timestampRaw = readHeaderOrNull(request, HEADER_TIMESTAMP);
        String nonce = readHeaderOrNull(request, HEADER_NONCE);
        String providedSignature = readHeaderOrNull(request, HEADER_SIGNATURE);

        if (partnerId == null) {
            return PartnerAuthResult.unauthorized("Missing header: " + HEADER_PARTNER_ID);
        }
        if (timestampRaw == null) {
            return PartnerAuthResult.unauthorized("Missing header: " + HEADER_TIMESTAMP);
        }
        if (nonce == null) {
            return PartnerAuthResult.unauthorized("Missing header: " + HEADER_NONCE);
        }
        if (providedSignature == null) {
            return PartnerAuthResult.unauthorized("Missing header: " + HEADER_SIGNATURE);
        }

        Map<String, String> partnerSecrets = ssoProperties.getPartnerSecrets();
        String secret = partnerSecrets.get(partnerId);
        if (secret == null || secret.isBlank()) {
            return PartnerAuthResult.unauthorized("Unknown partner");
        }

        long nowEpochSeconds = Instant.now(clock).getEpochSecond();
        long timestampSeconds;
        try {
            timestampSeconds = Long.parseLong(timestampRaw);
        } catch (NumberFormatException e) {
            return PartnerAuthResult.unauthorized("Invalid timestamp");
        }

        long allowedSkew = Math.max(0, ssoProperties.getTimestampSkewSeconds());
        if (Math.abs(nowEpochSeconds - timestampSeconds) > allowedSkew) {
            return PartnerAuthResult.unauthorized("Request expired");
        }

        String method = request.getMethod().toUpperCase(Locale.ROOT);
        String path = request.getRequestURI();

        byte[] bodyBytes = rawBody == null ? new byte[0] : rawBody.getBytes(StandardCharsets.UTF_8);
        String bodySha256Hex = sha256Hex(bodyBytes);

        String canonical = method + "\n" + path + "\n" + timestampRaw + "\n" + nonce + "\n" + bodySha256Hex;
        String expectedSignature = hmacBase64(secret, canonical);

        if (!constantTimeEquals(expectedSignature, providedSignature)) {
            return PartnerAuthResult.unauthorized("Invalid signature");
        }

        Duration nonceTtl = Duration.ofSeconds(Math.max(0, ssoProperties.getNonceTtlSeconds()));
        if (nonceTtl.isZero()) {
            return PartnerAuthResult.unauthorized("Replay protection disabled");
        }

        String nonceKey = buildNonceKey(partnerId, nonce);
        Boolean stored = stringRedisTemplate.opsForValue().setIfAbsent(nonceKey, "1", nonceTtl);
        if (Boolean.FALSE.equals(stored)) {
            return PartnerAuthResult.unauthorized("Replay detected");
        }

        return PartnerAuthResult.ok(partnerId);
    }

    private static String readHeaderOrNull(HttpServletRequest request, String name) {
        String value = request.getHeader(name);
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private static String buildNonceKey(String partnerId, String nonce) {
        return "sso:nonce:" + partnerId + ":" + nonce;
    }

    private static String sha256Hex(byte[] bodyBytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(bodyBytes);
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
