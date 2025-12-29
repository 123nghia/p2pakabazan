package com.akabazan.admin.controller;

import com.akabazan.admin.security.UserAdminRepository;
import com.akabazan.common.dto.BaseResponse;
import com.akabazan.common.dto.ResponseFactory;
import com.akabazan.service.DisputeService;
import com.akabazan.service.dto.DisputeResult;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminDisputeController {

    private final DisputeService disputeService;
    private final UserAdminRepository userAdminRepository;
    private final com.akabazan.admin.service.CurrentAdminService currentAdminService;

    public AdminDisputeController(DisputeService disputeService,
            UserAdminRepository userAdminRepository,
            com.akabazan.admin.service.CurrentAdminService currentAdminService) {
        this.disputeService = disputeService;
        this.userAdminRepository = userAdminRepository;
        this.currentAdminService = currentAdminService;
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
        checkPermission(disputeId);
        DisputeResult result = disputeService.resolveDispute(disputeId, outcome, note);
        return ResponseFactory.ok(result);
    }

    @PostMapping("/disputes/{disputeId}/reject")
    public ResponseEntity<BaseResponse<DisputeResult>> rejectDispute(
            @PathVariable UUID disputeId,
            @RequestParam(value = "note", required = false) String note) {
        checkPermission(disputeId);
        DisputeResult result = disputeService.rejectDispute(disputeId, note);
        return ResponseFactory.ok(result);
    }

    private void checkPermission(UUID disputeId) {
        com.akabazan.admin.security.UserAdmin currentAdmin = currentAdminService.getCurrentAdmin()
                .orElseThrow(() -> new com.akabazan.common.exception.ApplicationException(
                        com.akabazan.common.constant.ErrorCode.UNAUTHORIZED));

        if (currentAdmin.getRole() == com.akabazan.repository.constant.AdminRole.SUPER_ADMIN) {
            return;
        }

        DisputeResult dispute = disputeService.getDisputeById(disputeId);
        // Compare by ID or Email. DisputeResult usually has assignedAdminId or similar.
        // Based on assignDispute logic, it uses UUID.
        // Let's check if currentAdmin.getId() matches.
        if (dispute.getAssignedAdminId() == null || !dispute.getAssignedAdminId().equals(currentAdmin.getId())) {
            throw new com.akabazan.common.exception.ApplicationException(
                    com.akabazan.common.constant.ErrorCode.FORBIDDEN);
        }
    }

    @PostMapping("/disputes/{disputeId}/assign")
    public ResponseEntity<BaseResponse<DisputeResult>> assignDispute(
            @PathVariable UUID disputeId,
            @RequestParam(value = "username", required = false) String username) {
        com.akabazan.admin.security.UserAdmin currentAdmin = currentAdminService.getCurrentAdmin()
                .orElseThrow(() -> new com.akabazan.common.exception.ApplicationException(
                        com.akabazan.common.constant.ErrorCode.UNAUTHORIZED));

        if (currentAdmin.getRole() != com.akabazan.repository.constant.AdminRole.SUPER_ADMIN) {
            throw new com.akabazan.common.exception.ApplicationException(
                    com.akabazan.common.constant.ErrorCode.FORBIDDEN);
        }

        DisputeResult result;
        if (username == null || username.isBlank()) {
            result = disputeService.assignToAdmin(disputeId, currentAdmin.getId());
        } else {
            UUID adminId = userAdminRepository.findByUsername(username)
                    .map(u -> u.getId())
                    .orElseThrow(() -> new com.akabazan.common.exception.ApplicationException(
                            com.akabazan.common.constant.ErrorCode.USER_NOT_FOUND));
            result = disputeService.assignToAdmin(disputeId, adminId);
        }
        return ResponseFactory.ok(result);
    }

}
