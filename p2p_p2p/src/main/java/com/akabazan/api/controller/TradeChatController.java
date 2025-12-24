package com.akabazan.api.controller;

import com.akabazan.api.mapper.TradeChatResponseMapper;
import com.akabazan.api.mapper.TradeChatThreadResponseMapper;
import com.akabazan.api.reponse.TradeChatResponse;
import com.akabazan.api.reponse.TradeChatThreadResponse;
import com.akabazan.api.request.ChatRequest;
import com.akabazan.api.reponse.DashboardCountResponse;
import com.akabazan.service.TradeChatService;
import com.akabazan.service.DashboardService;
import com.akabazan.service.dto.DashboardCounts;
import com.akabazan.service.dto.TradeChatResult;
import com.akabazan.service.dto.TradeChatThreadResult;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/p2p/trades")
public class TradeChatController extends BaseController {

    private final TradeChatService tradeChatService;
    private final DashboardService dashboardService;

    public TradeChatController(TradeChatService tradeChatService, DashboardService dashboardService) {
        this.tradeChatService = tradeChatService;
        this.dashboardService = dashboardService;
    }

    @PostMapping("/{tradeId}/chat")
    public ResponseEntity<TradeChatResponse> sendMessage(@PathVariable UUID tradeId,
            @RequestBody ChatRequest req) {
        TradeChatResult dto = tradeChatService.sendMessage(tradeId, req.getMessages(), req.getImage());
        return ResponseEntity.ok(TradeChatResponseMapper.from(dto));
    }

    @GetMapping("/{tradeId}/chat")
    public ResponseEntity<List<TradeChatResponse>> getMessages(
            @PathVariable UUID tradeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since) {
        List<TradeChatResult> messages = tradeChatService.getMessages(tradeId, since);
        return ResponseEntity.ok(TradeChatResponseMapper.fromList(messages));
    }

    @GetMapping("/chat/threads")
    public ResponseEntity<List<TradeChatThreadResponse>> getChatThreads() {
        List<TradeChatThreadResult> threads = tradeChatService.getChatThreadsForCurrentUser();
        return ResponseEntity.ok(TradeChatThreadResponseMapper.fromList(threads));
    }

    @GetMapping("/dashboard/counts")
    public ResponseEntity<DashboardCountResponse> getDashboardCounts() {
        DashboardCounts counts = dashboardService.getCountsForCurrentUser();
        DashboardCountResponse resp = new DashboardCountResponse();
        resp.setActiveTrades(counts.getActiveTrades());
        resp.setIncomingChats(counts.getIncomingChats());
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/{tradeId}/chat/read")
    public ResponseEntity<Void> markChatRead(@PathVariable UUID tradeId) {
        tradeChatService.markRead(tradeId);
        return ResponseEntity.ok().build();
    }
}
