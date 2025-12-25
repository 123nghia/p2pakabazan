package com.akabazan.service;

import com.akabazan.service.dto.response.DisputeReasonResponse;

import java.util.List;

public interface DisputeReasonService {
    List<DisputeReasonResponse> getDisputeReasons(String role);
}
