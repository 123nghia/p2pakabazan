package com.akabazan.api.controller;

import com.akabazan.api.mapper.TradeCommandMapper;
import com.akabazan.api.mapper.TradeInfoResponseMapper;
import com.akabazan.api.mapper.TradeCreatedResponseMapper;
import com.akabazan.api.mapper.TradeResponseMapper;
import com.akabazan.api.reponse.TradeInfoResponse;
import com.akabazan.api.reponse.TradeCreatedResponse;
import com.akabazan.api.reponse.TradeResponse;
import com.akabazan.api.request.TradeRequest;
import com.akabazan.common.constant.ErrorCode;
import com.akabazan.common.dto.BaseResponse;
import com.akabazan.common.dto.ResponseFactory;
import com.akabazan.common.exception.ApplicationException;
import com.akabazan.service.CurrentUserService;
import com.akabazan.service.TradeService;
import com.akabazan.service.command.TradeCreateCommand;
import com.akabazan.service.dto.TradeInfoResult;
import com.akabazan.service.dto.TradeResult;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/p2p")

public class TradeController extends BaseController {

    private final TradeService tradeService;
    private final CurrentUserService currentUserService;

    public TradeController(TradeService tradeService, CurrentUserService currentUserService) {
        this.tradeService = tradeService;
        this.currentUserService = currentUserService;
    }

    // ====================== ORDER ======================
    @GetMapping("/trades/me")
    public ResponseEntity<BaseResponse<List<TradeResponse>>> getTradesOfCurrentUser() {
        var userId = currentUserService.getCurrentUserId()
                .orElseThrow(() -> new ApplicationException(ErrorCode.UNAUTHORIZED));

        List<TradeResult> trades = tradeService.getTradesByUser(userId);
        return ResponseFactory.ok(trades.stream().map(TradeResponseMapper::from).toList());
    }

    @GetMapping("/orders/{orderId}/trades")
    public  ResponseEntity < BaseResponse<List<TradeResponse>>> getTradesByOrder(@PathVariable UUID orderId) {
        List<TradeResult> trades = tradeService.getTradesByOrder(orderId);
        return ResponseFactory.ok(trades.stream().map(TradeResponseMapper::from).toList());
    }

    @GetMapping("/trades/order/{orderId}")
    public ResponseEntity<BaseResponse<List<TradeResponse>>> getTradesByOrderId(@PathVariable UUID orderId) {
        List<TradeResult> trades = tradeService.getTradesByOrder(orderId);
        return ResponseFactory.ok(trades.stream().map(TradeResponseMapper::from).toList());
    }


    @PostMapping("/trades")
    public ResponseEntity < BaseResponse<TradeCreatedResponse>> createTrade(@Valid @RequestBody TradeRequest tradeRequest) {
        TradeCreateCommand command = TradeCommandMapper.toCommand(tradeRequest); // map từ API request sang Command
        TradeResult result = tradeService.createTrade(command);
        return ResponseFactory.ok(TradeCreatedResponseMapper.from(result));
    }

    @PostMapping("/trades/{tradeId}/confirm-payment")
    public ResponseEntity<BaseResponse<TradeResponse>> confirmPayment(@PathVariable UUID tradeId) {
        TradeResult result = tradeService.confirmPayment(tradeId);
        return ResponseFactory.ok(TradeResponseMapper.from(result));
    }

    @PostMapping("/trades/{tradeId}/confirm-received")
    public ResponseEntity<BaseResponse<TradeResponse>> confirmReceived(@PathVariable UUID tradeId) {
        TradeResult result = tradeService.confirmReceived(tradeId);
        return ResponseFactory.ok(TradeResponseMapper.from(result));
    }

    @PostMapping("/trades/{tradeId}/cancel")
    public ResponseEntity<BaseResponse<TradeResponse>> cancelTrade(@PathVariable UUID tradeId) {

        TradeResult result = tradeService.cancelTrade(tradeId);
        return ResponseFactory.ok(TradeResponseMapper.from(result));
    }

    @PostMapping("/trades/code/{tradeCode}/cancel")
    public ResponseEntity<BaseResponse<TradeResponse>> cancelTradeByCode(@PathVariable String tradeCode) {
        TradeResult result = tradeService.cancelTradeByCode(tradeCode);
        return ResponseFactory.ok(TradeResponseMapper.from(result));
    }


    @GetMapping("/trades/{tradeId}")
    public ResponseEntity<BaseResponse<TradeInfoResponse>> getTradeInfo(@PathVariable UUID tradeId) {
        TradeInfoResult info = tradeService.getTradeInfo(tradeId);
        return ResponseFactory.ok(TradeInfoResponseMapper.from(info));
    }
}
