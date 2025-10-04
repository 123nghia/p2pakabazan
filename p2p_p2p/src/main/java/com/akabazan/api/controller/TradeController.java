package com.akabazan.api.controller;

import com.akabazan.api.mapper.TradeCommandMapper;
import com.akabazan.api.mapper.TradeResponseMapper;
import com.akabazan.api.reponse.TradeResponse;
import com.akabazan.api.request.TradeRequest;
import com.akabazan.common.dto.BaseResponse;
import com.akabazan.common.dto.ResponseFactory;
import com.akabazan.service.TradeService;
import com.akabazan.service.command.TradeCreateCommand;
import com.akabazan.service.dto.TradeResult;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/p2p")
@CrossOrigin(origins = {
        "http://localhost:5500",
        "http://localhost:5174"
})

public class TradeController extends BaseController {

    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    // ====================== ORDER ======================

   
    @GetMapping("/orders/{orderId}/trades")
    public  ResponseEntity < BaseResponse<List<TradeResponse>>> getTradesByOrder(@PathVariable Long orderId) {
        List<TradeResult> trades = tradeService.getTradesByOrder(orderId);
        return ResponseFactory.ok(trades.stream().map(TradeResponseMapper::from).toList());
    }

    @PostMapping("/trades")
    public ResponseEntity < BaseResponse<TradeResponse>> createTrade(@RequestBody TradeRequest tradeRequest) {
        TradeCreateCommand command = TradeCommandMapper.toCommand(tradeRequest); // map từ API request sang Command
        TradeResult result = tradeService.createTrade(command);
        return ResponseFactory.ok(TradeResponseMapper.from(result));
    }

    @PostMapping("/trades/{tradeId}/confirm-payment")
    public ResponseEntity<BaseResponse<TradeResponse>> confirmPayment(@PathVariable Long tradeId) {
        TradeResult result = tradeService.confirmPayment(tradeId);
        return ResponseFactory.ok(TradeResponseMapper.from(result));
    }

    @PostMapping("/trades/{tradeId}/confirm-received")
    public ResponseEntity<BaseResponse<TradeResponse>> confirmReceived(@PathVariable Long tradeId) {
        TradeResult result = tradeService.confirmReceived(tradeId);
        return ResponseFactory.ok(TradeResponseMapper.from(result));
    }

    @PostMapping("/trades/{tradeId}/cancel")
    public ResponseEntity<BaseResponse<TradeResponse>> cancelTrade(@PathVariable Long tradeId) {

        TradeResult result = tradeService.cancelTrade(tradeId);
        return ResponseFactory.ok(TradeResponseMapper.from(result));
    }

}
