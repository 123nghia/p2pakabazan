package com.akabazan.service.dto;

import com.akabazan.repository.entity.Dispute;

public class DisputeMapper {

    private DisputeMapper() {
    }

    public static DisputeResult toResult(Dispute dispute) {
        if (dispute == null) {
            return null;
        }
        DisputeResult result = new DisputeResult();
        result.setId(dispute.getId());
        result.setTradeId(dispute.getTrade().getId());
        result.setReason(dispute.getReason());
        result.setEvidence(dispute.getEvidence());
        result.setStatus(dispute.getStatus().name());
        if (dispute.getAssignedAdmin() != null) {
            result.setAssignedAdminId(dispute.getAssignedAdmin().getId());
            result.setAssignedAdminEmail(dispute.getAssignedAdmin().getEmail());
        }
        if (dispute.getResolutionOutcome() != null) {
            result.setResolutionOutcome(dispute.getResolutionOutcome().name());
        }
        result.setResolutionNote(dispute.getResolutionNote());
        result.setCreatedAt(dispute.getCreatedAt());
        result.setUpdatedAt(dispute.getUpdatedAt());
        result.setResolvedAt(dispute.getResolvedAt());
        return result;
    }
}
