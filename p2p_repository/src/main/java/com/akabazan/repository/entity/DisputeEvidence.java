package com.akabazan.repository.entity;

import com.akabazan.framework.data.domain.AuditEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "dispute_evidence")
public class DisputeEvidence extends AuditEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispute_id", nullable = false)
    private Dispute dispute;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;

    public DisputeEvidence() {
    }

    public DisputeEvidence(Dispute dispute, String url) {
        this.dispute = dispute;
        this.url = url;
    }

    public Dispute getDispute() {
        return dispute;
    }

    public void setDispute(Dispute dispute) {
        this.dispute = dispute;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
