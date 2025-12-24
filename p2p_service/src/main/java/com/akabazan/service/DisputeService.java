package com.akabazan.service;

import com.akabazan.repository.entity.Dispute.DisputeStatus;
import com.akabazan.service.dto.DisputeResult;

import java.util.List;
import java.util.UUID;

public interface DisputeService {

    DisputeResult openDispute(UUID tradeId, String reason, String evidence);

    List<DisputeResult> getDisputesByTrade(UUID tradeId);

    List<DisputeResult> getDisputes(DisputeStatus status, boolean onlyAssignedToCurrentAdmin);

    DisputeResult assignToCurrentAdmin(UUID disputeId);

    DisputeResult assignToAdmin(UUID disputeId, UUID adminId);

    DisputeResult resolveDispute(UUID disputeId, String outcome, String resolutionNote);

    DisputeResult rejectDispute(UUID disputeId, String resolutionNote);

    DisputeResult getDisputeById(UUID disputeId);
}
