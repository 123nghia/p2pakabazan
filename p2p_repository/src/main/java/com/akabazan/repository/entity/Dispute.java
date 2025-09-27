package com.akabazan.repository.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "disputes")
public class Dispute extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_id", nullable = false)
    private Trade trade;

    @Column(nullable = false)
    private String reason;

    @Column
    private String evidence;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters & Setters
    public Trade getTrade() { return trade; }
    public void setTrade(Trade trade) { this.trade = trade; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getEvidence() { return evidence; }
    public void setEvidence(String evidence) { this.evidence = evidence; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
