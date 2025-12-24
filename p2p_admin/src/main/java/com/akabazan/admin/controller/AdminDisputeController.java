package com.akabazan.admin.controller;

import com.akabazan.admin.security.UserAdminRepository;
import com.akabazan.common.dto.BaseResponse;
import com.akabazan.common.dto.ResponseFactory;
import com.akabazan.service.DisputeService;
import com.akabazan.service.dto.DisputeResult;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminDisputeController {

    private final DisputeService disputeService;
    private final UserAdminRepository userAdminRepository;

    public AdminDisputeController(DisputeService disputeService, UserAdminRepository userAdminRepository) {
        this.disputeService = disputeService;
        this.userAdminRepository = userAdminRepository;
    }

    @GetMapping("/disputes")
    public ResponseEntity<BaseResponse<List<DisputeResult>>> listDisputes(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "onlyMine", required = false, defaultValue = "false") boolean onlyMine) {
        com.akabazan.repository.entity.Dispute.DisputeStatus disputeStatus = null;
        if (status != null && !status.isBlank()) {
            disputeStatus = com.akabazan.repository.entity.Dispute.DisputeStatus.valueOf(status);
        }
        List<DisputeResult> disputes = disputeService.getDisputes(disputeStatus, onlyMine);
        return ResponseFactory.ok(disputes);
    }

    @GetMapping("/disputes/{disputeId}")
    public ResponseEntity<BaseResponse<DisputeResult>> getDisputeById(@PathVariable UUID disputeId) {
        DisputeResult result = disputeService.getDisputeById(disputeId);
        return ResponseFactory.ok(result);
    }

    @PostMapping("/disputes/{disputeId}/resolve")
    public ResponseEntity<BaseResponse<DisputeResult>> resolveDispute(
            @PathVariable UUID disputeId,
            @RequestParam("outcome") String outcome,
            @RequestParam(value = "note", required = false) String note) {
        DisputeResult result = disputeService.resolveDispute(disputeId, outcome, note);
        return ResponseFactory.ok(result);
    }

    @PostMapping("/disputes/{disputeId}/reject")
    public ResponseEntity<BaseResponse<DisputeResult>> rejectDispute(
            @PathVariable UUID disputeId,
            @RequestParam(value = "note", required = false) String note) {
        DisputeResult result = disputeService.rejectDispute(disputeId, note);
        return ResponseFactory.ok(result);
    }

    @PostMapping("/disputes/{disputeId}/assign")
    public ResponseEntity<BaseResponse<DisputeResult>> assignDispute(
            @PathVariable UUID disputeId,
            @RequestParam(value = "adminId", required = false) UUID adminId) {
        DisputeResult result;
        if (adminId == null) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UUID currentAdminId;
            if (principal instanceof UUID) {
                currentAdminId = (UUID) principal;
            } else {
                try {
                    currentAdminId = UUID.fromString(principal.toString());
                } catch (IllegalArgumentException e) {
                    currentAdminId = userAdminRepository.findByUsername(principal.toString())
                            .map(u -> u.getId())
                            .orElseThrow(() -> new com.akabazan.common.exception.ApplicationException(
                                    com.akabazan.common.constant.ErrorCode.USER_NOT_FOUND));
                }
            }
            result = disputeService.assignToAdmin(disputeId, currentAdminId);
        } else {
            result = disputeService.assignToAdmin(disputeId, adminId);
        }
        return ResponseFactory.ok(result);
    }
}
