package com.akabazan.service.dto;

import com.akabazan.repository.entity.Dispute;

public class DisputeMapper {

    public static DisputeDTO toDTO(Dispute dispute) {
        if (dispute == null) return null;
        DisputeDTO dto = new DisputeDTO();
        dto.setId(dispute.getId());
        dto.setTradeId(dispute.getTrade().getId());
        dto.setReason(dispute.getReason());
        dto.setEvidence(dispute.getEvidence());
        dto.setCreatedAt(dispute.getCreatedAt());
        return dto;
    }
}
