package com.akabazan.service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class DisputeResult {
    private UUID id;
    private UUID tradeId;
    private String reason;
    private String evidence;
    private String status;
    private UUID assignedAdminId;
    private String assignedAdminEmail;
    private String resolutionOutcome;
    private String resolutionNote;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTradeId() {
        return tradeId;
    }

    public void setTradeId(UUID tradeId) {
        this.tradeId = tradeId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UUID getAssignedAdminId() {
        return assignedAdminId;
    }

    public void setAssignedAdminId(UUID assignedAdminId) {
        this.assignedAdminId = assignedAdminId;
    }

    public String getAssignedAdminEmail() {
        return assignedAdminEmail;
    }

    public void setAssignedAdminEmail(String assignedAdminEmail) {
        this.assignedAdminEmail = assignedAdminEmail;
    }

    public String getResolutionOutcome() {
        return resolutionOutcome;
    }

    public void setResolutionOutcome(String resolutionOutcome) {
        this.resolutionOutcome = resolutionOutcome;
    }

    public String getResolutionNote() {
        return resolutionNote;
    }

    public void setResolutionNote(String resolutionNote) {
        this.resolutionNote = resolutionNote;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
}
