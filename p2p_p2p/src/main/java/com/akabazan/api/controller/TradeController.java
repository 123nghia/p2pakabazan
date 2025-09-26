package com.akabazan.api.controller;

import com.akabazan.service.TradeService;
import com.akabazan.service.dto.TradeDTO;
import com.akabazan.service.dto.ChatMessageDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trades")
public class TradeController {

    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @PostMapping
    public ResponseEntity<TradeDTO> createTrade(@RequestBody TradeDTO tradeDTO) {
        return ResponseEntity.ok(tradeService.createTrade(tradeDTO));
    }

    @PostMapping("/{tradeId}/confirm-payment")
    public ResponseEntity<TradeDTO> confirmPayment(@PathVariable Long tradeId) {
        return ResponseEntity.ok(tradeService.confirmPayment(tradeId));
    }

    @PostMapping("/{tradeId}/confirm-received")
    public ResponseEntity<TradeDTO> confirmReceived(@PathVariable Long tradeId) {
        return ResponseEntity.ok(tradeService.confirmReceived(tradeId));
    }

    @PostMapping("/{tradeId}/chat")
    public ResponseEntity<TradeDTO> sendChatMessage(@PathVariable Long tradeId,
                                                    @RequestBody ChatMessageDTO messageDTO) {
        return ResponseEntity.ok(tradeService.sendChatMessage(tradeId, messageDTO));
    }

    @PostMapping("/{tradeId}/dispute")
    public ResponseEntity<TradeDTO> openDispute(@PathVariable Long tradeId,
                                                @RequestParam String reason,
                                                @RequestParam String evidence) {
        return ResponseEntity.ok(tradeService.openDispute(tradeId, reason, evidence));
    }
}
