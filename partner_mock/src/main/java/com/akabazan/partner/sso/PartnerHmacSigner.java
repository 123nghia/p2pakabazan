package com.akabazan.partner.sso;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.Locale;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Component;

@Component
public class PartnerHmacSigner {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    public SignedHeaders sign(String secret, String method, String path, String body) {
        String timestamp = String.valueOf(Instant.now().getEpochSecond());
        String nonce = UUID.randomUUID().toString();

        String canonical = buildCanonicalString(
                method,
                path,
                timestamp,
                nonce,
                body == null ? "" : body);

        String signature = hmacBase64(secret, canonical);
        return new SignedHeaders(timestamp, nonce, signature);
    }

    private static String buildCanonicalString(String method, String path, String timestamp, String nonce, String body) {
        String normalizedMethod = method == null ? "" : method.toUpperCase(Locale.ROOT);
        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
        String bodySha256Hex = sha256Hex(bodyBytes);
        return normalizedMethod + "\n" + path + "\n" + timestamp + "\n" + nonce + "\n" + bodySha256Hex;
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

    public record SignedHeaders(String timestamp, String nonce, String signature) {}
}
