package com.akabazan.service;

import com.akabazan.service.dto.DisputeResult;

import java.util.List;

public interface DisputeService {

    DisputeResult openDispute(Long tradeId, String reason, String evidence);

    List<DisputeResult> getDisputesByTrade(Long tradeId);
}
