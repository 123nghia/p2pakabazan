package com.akabazan.service.dto;

import java.util.Objects;
import com.akabazan.repository.entity.User;

public class PartnerUserProvisioningCommand {

    private final String partnerId;
    private final String externalUserId;
    private final String fallbackEmail;
    private final String email;
    private final String username;
    private final User.KycStatus kycStatus;

    public PartnerUserProvisioningCommand(String partnerId,
                                          String externalUserId,
                                          String fallbackEmail,
                                          String email,
                                          String username,
                                          User.KycStatus kycStatus) {
        this.partnerId = Objects.requireNonNull(partnerId, "partnerId must not be null");
        this.externalUserId = Objects.requireNonNull(externalUserId, "externalUserId must not be null");
        this.fallbackEmail = Objects.requireNonNull(fallbackEmail, "fallbackEmail must not be null");
        this.email = email;
        this.username = username;
        this.kycStatus = kycStatus;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public String getExternalUserId() {
        return externalUserId;
    }

    public String getFallbackEmail() {
        return fallbackEmail;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public User.KycStatus getKycStatus() {
        return kycStatus;
    }
}
