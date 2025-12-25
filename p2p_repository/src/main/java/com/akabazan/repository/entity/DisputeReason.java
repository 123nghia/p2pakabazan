package com.akabazan.repository.entity;

import com.akabazan.framework.data.domain.AuditEntity;
import com.akabazan.repository.constant.DisputePriority;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "dispute_reasons")
public class DisputeReason extends AuditEntity {

    @Column(name = "role", nullable = false, length = 50)
    private String role;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 20)
    private DisputePriority priority;

    @Column(name = "required_evidence", columnDefinition = "TEXT")
    private String requiredEvidence;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DisputePriority getPriority() {
        return priority;
    }

    public void setPriority(DisputePriority priority) {
        this.priority = priority;
    }

    public String getRequiredEvidence() {
        return requiredEvidence;
    }

    public void setRequiredEvidence(String requiredEvidence) {
        this.requiredEvidence = requiredEvidence;
    }
}
