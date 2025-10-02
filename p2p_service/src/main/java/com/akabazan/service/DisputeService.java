package com.akabazan.service;

import com.akabazan.repository.entity.Dispute.DisputeStatus;
import com.akabazan.service.dto.DisputeResult;

import java.util.List;

public interface DisputeService {

    DisputeResult openDispute(Long tradeId, String reason, String evidence);

    List<DisputeResult> getDisputesByTrade(Long tradeId);

    List<DisputeResult> getDisputes(DisputeStatus status, boolean onlyAssignedToCurrentAdmin);

    DisputeResult assignToCurrentAdmin(Long disputeId);

    DisputeResult assignToAdmin(Long disputeId, Long adminId);

    DisputeResult resolveDispute(Long disputeId, String outcome, String resolutionNote);

    DisputeResult rejectDispute(Long disputeId, String resolutionNote);
}
