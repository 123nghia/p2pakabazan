package com.akabazan.service.dto;

import com.akabazan.repository.entity.User.KycStatus;
import java.util.Objects;

public class IntegrationSyncCommand {

    private final String email;
    private final IntegrationWalletData walletData;
    private final KycStatus kycStatus;

    public IntegrationSyncCommand(String email, IntegrationWalletData walletData, KycStatus kycStatus) {
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.walletData = Objects.requireNonNull(walletData, "walletData must not be null");
        this.kycStatus = kycStatus;
    }

    public String getEmail() {
        return email;
    }

    public IntegrationWalletData getWalletData() {
        return walletData;
    }

    public KycStatus getKycStatus() {
        return kycStatus;
    }
}
