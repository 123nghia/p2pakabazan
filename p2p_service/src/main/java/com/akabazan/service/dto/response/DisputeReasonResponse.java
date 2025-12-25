package com.akabazan.service.dto.response;

import com.akabazan.repository.constant.DisputePriority;
import lombok.Data;

import java.util.UUID;

@Data
public class DisputeReasonResponse {
    private UUID id;
    private String role;
    private String description;
    private DisputePriority priority;
    private String requiredEvidence;
}
