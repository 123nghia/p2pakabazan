package com.akabazan.api.mapper;

import com.akabazan.api.reponse.DisputeResponse;
import com.akabazan.service.dto.DisputeResult;
import java.util.List;
import java.util.stream.Collectors;

public final class DisputeResponseMapper {

    private DisputeResponseMapper() {
    }

    public static DisputeResponse from(DisputeResult result) {
        if (result == null) {
            return null;
        }
        DisputeResponse response = new DisputeResponse();
        response.setId(result.getId());
        response.setTradeId(result.getTradeId());
        response.setReason(result.getReason());
        response.setEvidence(result.getEvidence());
        response.setEvidenceImages(result.getEvidenceImages());
        response.setStatus(result.getStatus());
        response.setAssignedAdminId(result.getAssignedAdminId());
        response.setAssignedAdminEmail(result.getAssignedAdminEmail());
        response.setResolutionOutcome(result.getResolutionOutcome());
        response.setResolutionNote(result.getResolutionNote());
        response.setCreatedAt(result.getCreatedAt());
        response.setUpdatedAt(result.getUpdatedAt());
        response.setResolvedAt(result.getResolvedAt());
        response.setCreatedByRole(result.getCreatedByRole());
        return response;
    }

    public static List<DisputeResponse> fromList(List<DisputeResult> results) {
        return results.stream()
                .map(DisputeResponseMapper::from)
                .collect(Collectors.toList());
    }
}
