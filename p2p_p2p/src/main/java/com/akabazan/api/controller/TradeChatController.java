package com.akabazan.api.controller;

import com.akabazan.service.TradeChatService;
import com.akabazan.service.dto.TradeChatDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/p2p/trades/{tradeId}/chat")
@CrossOrigin(origins = "http://localhost:5500")
public class TradeChatController {

    private final TradeChatService tradeChatService;

    public TradeChatController(TradeChatService tradeChatService) {
        this.tradeChatService = tradeChatService;
    }

    @PostMapping
    public ResponseEntity<TradeChatDTO> sendMessage(@PathVariable Long tradeId,
                                                    @RequestBody String message) {
        TradeChatDTO dto = tradeChatService.sendMessage(tradeId, message);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<TradeChatDTO>> getMessages(@PathVariable Long tradeId) {
        List<TradeChatDTO> messages = tradeChatService.getMessages(tradeId);
        return ResponseEntity.ok(messages);
    }
}
