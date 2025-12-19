package com.akabazan.service.partner;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.partner-api")
public class PartnerApiProperties {

    /**
     * Format: PARTNER_ID=http://partner-host:port[,PARTNER_ID2=http://...]
     */
    private String partners;

    private Map<String, String> partnerBaseUrls = Collections.emptyMap();

    private String clientId = "P2P_APP";

    private String clientSecret = "change-me";

    private int connectTimeoutMs = 5_000;

    private int readTimeoutMs = 10_000;

    public String getPartners() {
        return partners;
    }

    public void setPartners(String partners) {
        this.partners = partners;
        this.partnerBaseUrls = parsePartnerBaseUrls(partners);
    }

    public Map<String, String> getPartnerBaseUrls() {
        return partnerBaseUrls;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public int getConnectTimeoutMs() {
        return connectTimeoutMs;
    }

    public void setConnectTimeoutMs(int connectTimeoutMs) {
        this.connectTimeoutMs = connectTimeoutMs;
    }

    public int getReadTimeoutMs() {
        return readTimeoutMs;
    }

    public void setReadTimeoutMs(int readTimeoutMs) {
        this.readTimeoutMs = readTimeoutMs;
    }

    public String resolveBaseUrl(String partnerId) {
        if (partnerId == null || partnerId.isBlank()) {
            throw new IllegalArgumentException("partnerId must not be blank");
        }
        String url = partnerBaseUrls.get(partnerId.trim());
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("Partner baseUrl not configured for: " + partnerId);
        }
        return trimTrailingSlash(url);
    }

    private static Map<String, String> parsePartnerBaseUrls(String raw) {
        if (raw == null || raw.isBlank()) {
            return Collections.emptyMap();
        }

        Map<String, String> urls = new LinkedHashMap<>();
        String[] pairs = raw.split(",");
        for (String pair : pairs) {
            String trimmed = pair.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            int separator = trimmed.indexOf('=');
            if (separator <= 0 || separator == trimmed.length() - 1) {
                throw new IllegalArgumentException(
                        "Invalid app.partner-api.partners entry: '" + trimmed + "', expected PARTNER_ID=http://host:port");
            }
            String id = trimmed.substring(0, separator).trim();
            String url = trimmed.substring(separator + 1).trim();
            if (id.isEmpty() || url.isEmpty()) {
                throw new IllegalArgumentException(
                        "Invalid app.partner-api.partners entry: '" + trimmed + "', expected PARTNER_ID=http://host:port");
            }
            urls.put(id, trimTrailingSlash(url));
        }
        return Collections.unmodifiableMap(urls);
    }

    private static String trimTrailingSlash(String url) {
        if (url == null) {
            return "";
        }
        if (url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }
}

