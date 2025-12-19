package com.akabazan.api.sso.config;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.sso")
public class SsoProperties {

    /**
     * Format: PARTNER_ID:SHARED_SECRET[,PARTNER_ID2:SHARED_SECRET2]
     */
    private String partners;

    private long codeTtlSeconds = 60;

    private long nonceTtlSeconds = 300;

    private long timestampSkewSeconds = 300;

    private Map<String, String> partnerSecrets = Collections.emptyMap();

    public String getPartners() {
        return partners;
    }

    public void setPartners(String partners) {
        this.partners = partners;
        this.partnerSecrets = parsePartnerSecrets(partners);
    }

    public long getCodeTtlSeconds() {
        return codeTtlSeconds;
    }

    public void setCodeTtlSeconds(long codeTtlSeconds) {
        this.codeTtlSeconds = codeTtlSeconds;
    }

    public long getNonceTtlSeconds() {
        return nonceTtlSeconds;
    }

    public void setNonceTtlSeconds(long nonceTtlSeconds) {
        this.nonceTtlSeconds = nonceTtlSeconds;
    }

    public long getTimestampSkewSeconds() {
        return timestampSkewSeconds;
    }

    public void setTimestampSkewSeconds(long timestampSkewSeconds) {
        this.timestampSkewSeconds = timestampSkewSeconds;
    }

    public Map<String, String> getPartnerSecrets() {
        return partnerSecrets;
    }

    private static Map<String, String> parsePartnerSecrets(String rawPartners) {
        if (rawPartners == null || rawPartners.isBlank()) {
            return Collections.emptyMap();
        }

        Map<String, String> secrets = new LinkedHashMap<>();
        String[] pairs = rawPartners.split(",");
        for (String pair : pairs) {
            String trimmed = pair.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            int separatorIndex = trimmed.indexOf(':');
            if (separatorIndex <= 0 || separatorIndex == trimmed.length() - 1) {
                throw new IllegalArgumentException(
                        "Invalid app.sso.partners entry: '" + trimmed + "', expected PARTNER_ID:SHARED_SECRET");
            }
            String partnerId = trimmed.substring(0, separatorIndex).trim();
            String secret = trimmed.substring(separatorIndex + 1).trim();
            if (partnerId.isEmpty() || secret.isEmpty()) {
                throw new IllegalArgumentException(
                        "Invalid app.sso.partners entry: '" + trimmed + "', expected PARTNER_ID:SHARED_SECRET");
            }
            secrets.put(partnerId, secret);
        }
        return Collections.unmodifiableMap(secrets);
    }
}

