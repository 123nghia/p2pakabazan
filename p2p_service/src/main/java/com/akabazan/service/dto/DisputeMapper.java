package com.akabazan.service.dto;

import com.akabazan.repository.entity.Dispute;
import com.akabazan.repository.entity.DisputeEvidence;
import java.util.stream.Collectors;

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
        if (dispute.getEvidenceImages() != null) {
            result.setEvidenceImages(dispute.getEvidenceImages().stream()
                    .map(DisputeEvidence::getUrl)
                    .collect(Collectors.toList()));
        }
        result.setStatus(dispute.getStatus().name());
        if (dispute.getAssignedAdmin() != null) {
            result.setAssignedAdminId(dispute.getAssignedAdmin().getId());
            result.setAssignedAdminEmail(dispute.getAssignedAdmin().getUsername());
        }
        if (dispute.getResolutionOutcome() != null) {
            result.setResolutionOutcome(dispute.getResolutionOutcome().name());
        }
        result.setResolutionNote(dispute.getResolutionNote());
        result.setCreatedAt(dispute.getCreatedAt());
        result.setUpdatedAt(dispute.getUpdatedAt());
        result.setResolvedAt(dispute.getResolvedAt());
        if (dispute.getTrade() != null && dispute.getCreatedBy() != null) {
            java.util.UUID creatorId = dispute.getCreatedBy();
            if (creatorId.equals(dispute.getTrade().getBuyer().getId())) {
                result.setCreatedByRole("BUYER");
            } else if (creatorId.equals(dispute.getTrade().getSeller().getId())) {
                result.setCreatedByRole("SELLER");
            } else {
                result.setCreatedByRole("UNKNOWN");
            }
        }
        return result;
    }
}
