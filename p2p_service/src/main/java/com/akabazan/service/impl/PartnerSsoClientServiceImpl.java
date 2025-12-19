package com.akabazan.service.impl;

import com.akabazan.repository.PartnerSsoClientRepository;
import com.akabazan.repository.entity.PartnerSsoClient;
import com.akabazan.service.PartnerSsoClientService;
import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class PartnerSsoClientServiceImpl implements PartnerSsoClientService {

    private static final int SECRET_BYTES = 32;

    private final PartnerSsoClientRepository partnerSsoClientRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public PartnerSsoClientServiceImpl(PartnerSsoClientRepository partnerSsoClientRepository) {
        this.partnerSsoClientRepository = partnerSsoClientRepository;
    }

    @Override
    @Transactional
    public PartnerCredentials createPartner(String partnerId) {
        String normalizedPartnerId = requireTrimmed(partnerId, "partnerId");
        if (normalizedPartnerId.length() > 50) {
            throw new IllegalArgumentException("partnerId must be <= 50 characters");
        }
        if (partnerSsoClientRepository.existsByPartnerId(normalizedPartnerId)) {
            throw new IllegalStateException("Partner already exists: " + normalizedPartnerId);
        }

        String secret = generateSecret();

        PartnerSsoClient client = new PartnerSsoClient();
        client.setPartnerId(normalizedPartnerId);
        client.setSharedSecret(secret);
        partnerSsoClientRepository.save(client);

        return new PartnerCredentials(normalizedPartnerId, secret);
    }

    @Override
    public Optional<String> findSharedSecret(String partnerId) {
        String normalizedPartnerId = requireTrimmedOrNull(partnerId);
        if (normalizedPartnerId == null) {
            return Optional.empty();
        }
        return partnerSsoClientRepository.findByPartnerId(normalizedPartnerId)
                .map(PartnerSsoClient::getSharedSecret)
                .filter(secret -> secret != null && !secret.isBlank());
    }

    private String generateSecret() {
        byte[] bytes = new byte[SECRET_BYTES];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String requireTrimmed(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value.trim();
    }

    private static String requireTrimmedOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}

