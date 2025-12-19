package com.akabazan.partner.sso;

import com.akabazan.partner.config.P2pProperties;
import com.akabazan.partner.config.PartnerProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Component
public class P2pSsoClient {

    private static final String ISSUE_PATH = "/api/sso/issue";

    private final PartnerProperties partnerProperties;
    private final P2pProperties p2pProperties;
    private final PartnerHmacSigner signer;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public P2pSsoClient(PartnerProperties partnerProperties,
                        P2pProperties p2pProperties,
                        PartnerHmacSigner signer,
                        ObjectMapper objectMapper) {
        this.partnerProperties = partnerProperties;
        this.p2pProperties = p2pProperties;
        this.signer = signer;
        this.restTemplate = buildRestTemplate();
        this.objectMapper = objectMapper;
    }

    private static RestTemplate buildRestTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5_000);
        requestFactory.setReadTimeout(10_000);
        return new RestTemplate(requestFactory);
    }

    public String issueOneTimeCode(SsoIssueRequest request) {
        String body;
        try {
            body = objectMapper.writeValueAsString(request);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to serialize SSO request", e);
        }

        URI uri = URI.create(trimTrailingSlash(p2pProperties.getBaseUrl()) + ISSUE_PATH);
        int maxAttempts = 5;
        long backoffMillis = 400;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                PartnerHmacSigner.SignedHeaders signed = signer.sign(
                        partnerProperties.getSharedSecret(),
                        "POST",
                        ISSUE_PATH,
                        body);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.add("X-Partner-Id", partnerProperties.getId());
                headers.add("X-Timestamp", signed.timestamp());
                headers.add("X-Nonce", signed.nonce());
                headers.add("X-Signature", signed.signature());

                ResponseEntity<JsonNode> response = restTemplate.exchange(
                        uri,
                        HttpMethod.POST,
                        new HttpEntity<>(body, headers),
                        JsonNode.class);

                JsonNode root = response.getBody();
                String code = root == null ? null : root.path("result").path("code").asText(null);
                if (code == null || code.isBlank()) {
                    throw new IllegalStateException("Missing code from P2P response: " + (root == null ? "<null>" : root.toString()));
                }
                return code;
            } catch (ResourceAccessException e) {
                if (attempt >= maxAttempts) {
                    throw new IllegalStateException("Cannot connect to P2P at " + uri
                            + " (is p2p_p2p running and listening?)", e);
                }
                try {
                    Thread.sleep(backoffMillis);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Interrupted while waiting to retry P2P call", ie);
                }
                backoffMillis = Math.min(backoffMillis * 2, 2_000);
            } catch (RestClientResponseException e) {
                String responseBody = e.getResponseBodyAsString();
                throw new IllegalStateException(
                        "P2P /api/sso/issue failed: HTTP " + e.getRawStatusCode() + " body=" + responseBody, e);
            } catch (Exception e) {
                throw new IllegalStateException("Unable to issue one-time code via P2P", e);
            }
        }

        throw new IllegalStateException("Unable to issue one-time code via P2P");
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

    public record SsoIssueRequest(String externalUserId, String email, String username, String kycStatus) {}
}
