package com.akabazan.repository.entity;

import com.akabazan.framework.data.domain.AuditEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "disputes")
public class Dispute extends AuditEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_id", nullable = false)
    private Trade trade;

    @Column(nullable = false)
    private String reason;

    @Column
    private String evidence;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DisputeStatus status = DisputeStatus.OPEN;

    @OneToMany(mappedBy = "dispute", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DisputeEvidence> evidenceImages = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_admin_id")
    private AdminUser assignedAdmin;

    @Enumerated(EnumType.STRING)
    @Column(name = "resolution_outcome")
    private ResolutionOutcome resolutionOutcome;

    @Column(name = "resolution_note")
    private String resolutionNote;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    // Getters & Setters
    public Trade getTrade() {
        return trade;
    }

    public void setTrade(Trade trade) {
        this.trade = trade;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getEvidence() {
        return evidence;
    }

    public void setEvidence(String evidence) {
        this.evidence = evidence;
    }

    public DisputeStatus getStatus() {
        return status;
    }

    public void setStatus(DisputeStatus status) {
        this.status = status;
    }

    public AdminUser getAssignedAdmin() {
        return assignedAdmin;
    }

    public void setAssignedAdmin(AdminUser assignedAdmin) {
        this.assignedAdmin = assignedAdmin;
    }

    public ResolutionOutcome getResolutionOutcome() {
        return resolutionOutcome;
    }

    public void setResolutionOutcome(ResolutionOutcome resolutionOutcome) {
        this.resolutionOutcome = resolutionOutcome;
    }

    public String getResolutionNote() {
        return resolutionNote;
    }

    public void setResolutionNote(String resolutionNote) {
        this.resolutionNote = resolutionNote;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public Set<DisputeEvidence> getEvidenceImages() {
        return evidenceImages;
    }

    public void setEvidenceImages(Set<DisputeEvidence> evidenceImages) {
        this.evidenceImages = evidenceImages;
    }

    public enum DisputeStatus {
        OPEN, IN_REVIEW, RESOLVED, REJECTED
    }

    public enum ResolutionOutcome {
        BUYER_FAVORED, SELLER_FAVORED, CANCELLED
    }
}
