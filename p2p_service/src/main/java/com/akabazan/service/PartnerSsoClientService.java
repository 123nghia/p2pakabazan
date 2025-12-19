package com.akabazan.service;

import java.util.Optional;

public interface PartnerSsoClientService {

    PartnerCredentials createPartner(String partnerId);

    Optional<String> findSharedSecret(String partnerId);

    record PartnerCredentials(String partnerId, String sharedSecret) {}
}

