package com.akabazan.api.controller;

import com.akabazan.api.dto.DisputeResponse;
import com.akabazan.api.mapper.DisputeResponseMapper;
import com.akabazan.service.DisputeService;
import com.akabazan.service.dto.DisputeResult;
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
    public ResponseEntity<DisputeResponse> openDispute(
            @PathVariable Long tradeId,
            @RequestParam String reason,
            @RequestParam(required = false) String evidence) {

        DisputeResult result = disputeService.openDispute(tradeId, reason, evidence);
        return ResponseEntity.ok(DisputeResponseMapper.from(result));
    }

    @GetMapping("/trades/{tradeId}/disputes")
    public ResponseEntity<List<DisputeResponse>> getDisputesByTrade(@PathVariable Long tradeId) {
        List<DisputeResult> disputes = disputeService.getDisputesByTrade(tradeId);
        return ResponseEntity.ok(DisputeResponseMapper.fromList(disputes));
    }
}
