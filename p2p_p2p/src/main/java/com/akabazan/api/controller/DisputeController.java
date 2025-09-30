package com.akabazan.api.controller;

import com.akabazan.service.DisputeService;
import com.akabazan.service.dto.DisputeDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/p2p")
@CrossOrigin(origins = "http://localhost:5500")
public class DisputeController {

    private final DisputeService disputeService;

    public DisputeController(DisputeService disputeService) {
        this.disputeService = disputeService;
    }

    @PostMapping("/trades/{tradeId}/dispute")
    public ResponseEntity<DisputeDTO> openDispute(
            @PathVariable Long tradeId,
            @RequestParam String reason,
            @RequestParam(required = false) String evidence) {

        DisputeDTO result = disputeService.openDispute(tradeId, reason, evidence);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/trades/{tradeId}/disputes")
    public ResponseEntity<List<DisputeDTO>> getDisputesByTrade(@PathVariable Long tradeId) {
        List<DisputeDTO> disputes = disputeService.getDisputesByTrade(tradeId);
        return ResponseEntity.ok(disputes);
    }
}
