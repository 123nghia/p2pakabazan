package com.akabazan.service.partner;

import com.akabazan.service.partner.dto.BalancesResponse;
import com.akabazan.service.partner.dto.LockFundsRequest;
import com.akabazan.service.partner.dto.LockFundsResponse;
import com.akabazan.service.partner.dto.TransferFundsRequest;
import com.akabazan.service.partner.dto.TransferFundsResponse;
import com.akabazan.service.partner.dto.UnlockFundsRequest;
import com.akabazan.service.partner.dto.UnlockFundsResponse;
import com.akabazan.common.exception.ApplicationException;
import com.akabazan.common.constant.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class PartnerFundsApiClient {

    private static final String LOCK_PATH = "/internal/p2p/funds/lock";
    private static final String UNLOCK_PATH = "/internal/p2p/funds/unlock";
    private static final String TRANSFER_PATH = "/internal/p2p/funds/transfer";

    private final PartnerApiProperties properties;
    private final P2pClientHmacSigner signer;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public PartnerFundsApiClient(PartnerApiProperties properties,
            P2pClientHmacSigner signer,
            ObjectMapper objectMapper) {
        this.properties = properties;
        this.signer = signer;
        this.objectMapper = objectMapper;
        this.restTemplate = buildRestTemplate(properties);
    }

    public BalancesResponse getBalances(String partnerId, String externalUserId, String asset) {
        String baseUrl = properties.resolveBaseUrl(partnerId);
        String path = "/internal/p2p/users/" + externalUserId + "/balances";

        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl + path)
                .queryParam("asset", asset)
                .build(true)
                .toUri();

        HttpHeaders headers = signedHeaders("GET", path, "");
        ResponseEntity<BalancesResponse> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                BalancesResponse.class);
        return response.getBody();
    }

    public LockFundsResponse lock(String partnerId, LockFundsRequest request) {
        return postJson(partnerId, LOCK_PATH, request, LockFundsResponse.class);
    }

    public UnlockFundsResponse unlock(String partnerId, UnlockFundsRequest request) {
        return postJson(partnerId, UNLOCK_PATH, request, UnlockFundsResponse.class);
    }

    public TransferFundsResponse transfer(String partnerId, TransferFundsRequest request) {
        return postJson(partnerId, TRANSFER_PATH, request, TransferFundsResponse.class);
    }

    private <T> T postJson(String partnerId, String path, Object request, Class<T> responseType) {
        String baseUrl = properties.resolveBaseUrl(partnerId);
        try {
            String body = objectMapper.writeValueAsString(request);
            HttpHeaders headers = signedHeaders("POST", path, body);
            headers.setContentType(MediaType.APPLICATION_JSON);

            URI uri = URI.create(baseUrl + path);
            ResponseEntity<T> response = restTemplate.exchange(
                    uri,
                    HttpMethod.POST,
                    new HttpEntity<>(body, headers),
                    responseType);
            return response.getBody();
        } catch (RestClientResponseException e) {
            String body = e.getResponseBodyAsString();
            String message = "Partner API call failed";
            try {
                var errorNode = objectMapper.readTree(body);
                if (errorNode.has("message")) {
                    String partnerMsg = errorNode.get("message").asText();
                    if ("AMOUNT_EXCEEDS_LOCK".equals(partnerMsg)) {
                        message = "Số dư không đủ để thực hiện (AMOUNT_EXCEEDS_LOCK)";
                    } else {
                        message = partnerMsg;
                    }
                }
            } catch (Exception ignored) {
                message = "Partner API call failed";
            }
            throw new ApplicationException(ErrorCode.PARTNER_TRANSACTION_FAILED, message);
        } catch (Exception e) {
            throw new IllegalStateException("Partner API call failed: " + path, e);
        }
    }

    private HttpHeaders signedHeaders(String method, String path, String body) {
        P2pClientHmacSigner.SignedHeaders signed = signer.sign(
                properties.getClientSecret(),
                method,
                path,
                body);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-P2P-Id", properties.getClientId());
        headers.add("X-Timestamp", signed.timestamp());
        headers.add("X-Nonce", signed.nonce());
        headers.add("X-Signature", signed.signature());
        return headers;
    }

    private static RestTemplate buildRestTemplate(PartnerApiProperties properties) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Math.max(1_000, properties.getConnectTimeoutMs()));
        requestFactory.setReadTimeout(Math.max(1_000, properties.getReadTimeoutMs()));
        return new RestTemplate(requestFactory);
    }
}
