package com.akabazan.api.controller;

import com.akabazan.api.mapper.TradeChatResponseMapper;
import com.akabazan.api.reponse.TradeChatResponse;
import com.akabazan.api.request.ChatRequest;
import com.akabazan.service.TradeChatService;
import com.akabazan.service.dto.TradeChatResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/p2p/trades/{tradeId}/chat")

public class TradeChatController extends BaseController {

    private final TradeChatService tradeChatService;

    public TradeChatController(TradeChatService tradeChatService) {
        this.tradeChatService = tradeChatService;
    }

    @PostMapping
    public ResponseEntity<TradeChatResponse> sendMessage(@PathVariable Long tradeId,
                                                    @RequestBody ChatRequest req) {
        TradeChatResult dto = tradeChatService.sendMessage(tradeId, req.getMessages());
        return ResponseEntity.ok(TradeChatResponseMapper.from(dto));
    }

    @GetMapping
    public ResponseEntity<List<TradeChatResponse>> getMessages(@PathVariable Long tradeId) {
        List<TradeChatResult> messages = tradeChatService.getMessages(tradeId);
        return ResponseEntity.ok(TradeChatResponseMapper.fromList(messages));
    }
}
