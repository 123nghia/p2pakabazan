package com.akabazan.api.controller;

import com.akabazan.api.mapper.DisputeResponseMapper;
import com.akabazan.api.reponse.DisputeResponse;
import com.akabazan.api.request.DisputeAssignRequest;
import com.akabazan.api.request.DisputeRejectRequest;
import com.akabazan.api.request.DisputeResolutionRequest;
import com.akabazan.common.constant.ErrorCode;
import com.akabazan.common.exception.ApplicationException;
import com.akabazan.repository.entity.Dispute.DisputeStatus;
import com.akabazan.service.DisputeService;
import com.akabazan.service.dto.DisputeResult;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/p2p")

public class DisputeController extends BaseController {

    private final DisputeService disputeService;

    public DisputeController(DisputeService disputeService) {
        this.disputeService = disputeService;
    }

    @GetMapping("/disputes")
    public ResponseEntity<List<DisputeResponse>> getDisputes(
            @RequestParam(required = false) String status,
            @RequestParam(name = "onlyMine", defaultValue = "false") boolean onlyMine) {

        DisputeStatus disputeStatus = null;
        if (status != null && !status.isBlank()) {
            try {
                disputeStatus = DisputeStatus.valueOf(status.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new ApplicationException(ErrorCode.INVALID_DISPUTE_STATUS);
            }
        }

        List<DisputeResult> disputes = disputeService.getDisputes(disputeStatus, onlyMine);
        return ResponseEntity.ok(DisputeResponseMapper.fromList(disputes));
    }

    @PostMapping("/trades/{tradeId}/dispute")
    public ResponseEntity<DisputeResponse> openDispute(
            @PathVariable UUID tradeId,
            @RequestParam String reason,
            @RequestParam(required = false) String evidence) {

        DisputeResult result = disputeService.openDispute(tradeId, reason, evidence);
        return ResponseEntity.ok(DisputeResponseMapper.from(result));
    }

    @GetMapping("/trades/{tradeId}/disputes")
    public ResponseEntity<List<DisputeResponse>> getDisputesByTrade(@PathVariable UUID tradeId) {
        List<DisputeResult> disputes = disputeService.getDisputesByTrade(tradeId);
        return ResponseEntity.ok(DisputeResponseMapper.fromList(disputes));
    }

    @PostMapping("/disputes/{disputeId}/assign")
    public ResponseEntity<DisputeResponse> assignDispute(@PathVariable UUID disputeId,
                                                         @RequestBody(required = false) DisputeAssignRequest request) {
        DisputeResult result = (request == null || request.getAdminId() == null)
                ? disputeService.assignToCurrentAdmin(disputeId)
                : disputeService.assignToAdmin(disputeId, request.getAdminId());
        return ResponseEntity.ok(DisputeResponseMapper.from(result));
    }

    @PostMapping("/disputes/{disputeId}/resolve")
    public ResponseEntity<DisputeResponse> resolveDispute(@PathVariable UUID disputeId,
                                                          @RequestBody DisputeResolutionRequest request) {
        if (request == null || request.getOutcome() == null || request.getOutcome().isBlank()) {
            throw new ApplicationException(ErrorCode.INVALID_DISPUTE_STATUS);
        }
        DisputeResult result = disputeService.resolveDispute(disputeId, request.getOutcome(), request.getNote());
        return ResponseEntity.ok(DisputeResponseMapper.from(result));
    }

    @PostMapping("/disputes/{disputeId}/reject")
    public ResponseEntity<DisputeResponse> rejectDispute(@PathVariable UUID disputeId,
                                                         @RequestBody(required = false) DisputeRejectRequest request) {
        String note = request != null ? request.getNote() : null;
        DisputeResult result = disputeService.rejectDispute(disputeId, note);
        return ResponseEntity.ok(DisputeResponseMapper.from(result));
    }
}
