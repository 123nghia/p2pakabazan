package com.akabazan.partner.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "p2p.client")
public class P2pClientProperties {

    private String id;
    private String sharedSecret;
    private long timestampSkewSeconds = 300L;
    private long nonceTtlSeconds = 300L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSharedSecret() {
        return sharedSecret;
    }

    public void setSharedSecret(String sharedSecret) {
        this.sharedSecret = sharedSecret;
    }

    public long getTimestampSkewSeconds() {
        return timestampSkewSeconds;
    }

    public void setTimestampSkewSeconds(long timestampSkewSeconds) {
        this.timestampSkewSeconds = timestampSkewSeconds;
    }

    public long getNonceTtlSeconds() {
        return nonceTtlSeconds;
    }

    public void setNonceTtlSeconds(long nonceTtlSeconds) {
        this.nonceTtlSeconds = nonceTtlSeconds;
    }
}

