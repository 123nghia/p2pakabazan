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
        result.setCreatedAt(dispute.getCreatedAt());
        return result;
    }
}
