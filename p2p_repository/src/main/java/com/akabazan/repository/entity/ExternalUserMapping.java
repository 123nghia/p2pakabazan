package com.akabazan.repository.entity;

import com.akabazan.framework.data.domain.AuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "external_user_mappings",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_external_user_mappings_partner_external_user",
                        columnNames = {"partner_id", "external_user_id"})
        }
)
public class ExternalUserMapping extends AuditEntity {

    @Column(name = "partner_id", nullable = false, length = 50)
    private String partnerId;

    @Column(name = "external_user_id", nullable = false, length = 120)
    private String externalUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "external_username", length = 120)
    private String externalUsername;

    @Column(name = "external_email", length = 200)
    private String externalEmail;

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getExternalUserId() {
        return externalUserId;
    }

    public void setExternalUserId(String externalUserId) {
        this.externalUserId = externalUserId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getExternalUsername() {
        return externalUsername;
    }

    public void setExternalUsername(String externalUsername) {
        this.externalUsername = externalUsername;
    }

    public String getExternalEmail() {
        return externalEmail;
    }

    public void setExternalEmail(String externalEmail) {
        this.externalEmail = externalEmail;
    }
}

