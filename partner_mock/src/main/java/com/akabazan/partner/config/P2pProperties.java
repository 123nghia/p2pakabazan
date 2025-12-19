package com.akabazan.partner.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "p2p")
public class P2pProperties {

    private String baseUrl;
    private String uiUrl;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getUiUrl() {
        return uiUrl;
    }

    public void setUiUrl(String uiUrl) {
        this.uiUrl = uiUrl;
    }
}
