package com.akabazan.repository.entity;

import com.akabazan.framework.data.domain.AuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "partner_sso_clients",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_partner_sso_clients_partner_id",
                        columnNames = {"partner_id"})
        }
)
public class PartnerSsoClient extends AuditEntity {

    @Column(name = "partner_id", nullable = false, length = 50)
    private String partnerId;

    @Column(name = "shared_secret", nullable = false, length = 255)
    private String sharedSecret;

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

