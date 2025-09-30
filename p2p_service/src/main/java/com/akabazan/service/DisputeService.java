package com.akabazan.service;

import com.akabazan.service.dto.DisputeDTO;

import java.util.List;

public interface DisputeService {

    DisputeDTO openDispute(Long tradeId, String reason, String evidence);

    List<DisputeDTO> getDisputesByTrade(Long tradeId);
}
