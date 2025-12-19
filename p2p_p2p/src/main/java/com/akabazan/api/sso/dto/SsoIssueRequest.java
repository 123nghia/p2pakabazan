package com.akabazan.api.sso.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SsoIssueRequest {

    @NotBlank
    private String externalUserId;

    @Email
    @Size(max = 200)
    private String email;

    @Size(max = 120)
    private String username;

    @Pattern(regexp = "(?i)UNVERIFIED|PENDING|VERIFIED|REJECTED", message = "kycStatus must be one of UNVERIFIED, PENDING, VERIFIED, REJECTED")
    private String kycStatus;

    public String getExternalUserId() {
        return externalUserId;
    }

    public void setExternalUserId(String externalUserId) {
        this.externalUserId = externalUserId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getKycStatus() {
        return kycStatus;
    }

    public void setKycStatus(String kycStatus) {
        this.kycStatus = kycStatus;
    }
}
