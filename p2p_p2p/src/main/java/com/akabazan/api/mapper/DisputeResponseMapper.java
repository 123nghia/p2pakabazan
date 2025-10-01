package com.akabazan.api.mapper;

import com.akabazan.api.dto.DisputeResponse;
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
        response.setCreatedAt(result.getCreatedAt());
        return response;
    }

    public static List<DisputeResponse> fromList(List<DisputeResult> results) {
        return results.stream()
                .map(DisputeResponseMapper::from)
                .collect(Collectors.toList());
    }
}
