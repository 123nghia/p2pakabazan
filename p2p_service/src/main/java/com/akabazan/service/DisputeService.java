package com.akabazan.service;

import com.akabazan.service.dto.DisputeDTO;

public interface DisputeService {
    DisputeDTO openDispute(Long tradeId, String reason, String evidence);
}
