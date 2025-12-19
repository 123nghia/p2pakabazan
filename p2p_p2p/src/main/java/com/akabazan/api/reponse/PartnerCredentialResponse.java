package com.akabazan.api.reponse;

public class PartnerCredentialResponse {

    private String partnerId;
    private String sharedSecret;

    public PartnerCredentialResponse() {}

    public PartnerCredentialResponse(String partnerId, String sharedSecret) {
        this.partnerId = partnerId;
        this.sharedSecret = sharedSecret;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getSharedSecret() {
        return sharedSecret;
    }

    public void setSharedSecret(String sharedSecret) {
        this.sharedSecret = sharedSecret;
    }
}

