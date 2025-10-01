package com.akabazan.api.controller;

import com.akabazan.api.dto.OrderResponse;
import com.akabazan.api.dto.TradeResponse;
import com.akabazan.api.mapper.OrderCommandMapper;
import com.akabazan.api.mapper.OrderResponseMapper;
import com.akabazan.api.mapper.TradeCommandMapper;
import com.akabazan.api.mapper.TradeResponseMapper;
import com.akabazan.api.request.OrderRequest;
import com.akabazan.api.request.TradeRequest;
import com.akabazan.service.OrderService;
import com.akabazan.service.TradeService;
import com.akabazan.service.command.OrderCreateCommand;
import com.akabazan.service.command.TradeCreateCommand;
import com.akabazan.service.dto.OrderResult;
import com.akabazan.service.dto.TradeResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/p2p")
@CrossOrigin(origins = "http://localhost:5500") // Cho phép FE gọi

public class P2PController {
  
    private final OrderService orderService;
    private final TradeService tradeService;

    public P2PController(OrderService orderService, TradeService tradeService) {
        this.orderService = orderService;
        this.tradeService = tradeService;
    }

    // ====================== ORDER ======================

    @PostMapping("/orders")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest orderRequest) {
    // Map từ OrderRequest → Command cho service
    OrderCreateCommand command = OrderCommandMapper.toCommand(orderRequest);
    // Gọi service tạo order
    OrderResult result = orderService.createOrder(command);
    return ResponseEntity.ok(OrderResponseMapper.from(result));
    }



    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getOrders(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String token,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) String sortByPrice
    ) {
        List<OrderResult> orders = orderService.getOrders(type, token, paymentMethod, sortByPrice);
        return ResponseEntity.ok(OrderResponseMapper.fromList(orders));
    }

    @GetMapping("/orders/{orderId}/trades")
    public ResponseEntity<List<TradeResponse>> getTradesByOrder(@PathVariable Long orderId) {
        List<TradeResult> trades = tradeService.getTradesByOrder(orderId);
        return ResponseEntity.ok(trades.stream().map(TradeResponseMapper::from).toList());
    }

    /**
     * Người bán hủy order (chỉ khi chưa có trade).
     */
    
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok().build();
    }

    // ====================== TRADE ======================

    @PostMapping("/trades")
    public ResponseEntity<TradeResponse> createTrade(@RequestBody TradeRequest tradeRequest) {
        TradeCreateCommand command = TradeCommandMapper.toCommand(tradeRequest); // map từ API request sang Command
        TradeResult result = tradeService.createTrade(command);
        return ResponseEntity.ok(TradeResponseMapper.from(result));
    }

    @PostMapping("/trades/{tradeId}/confirm-payment")
    public ResponseEntity<TradeResponse> confirmPayment(@PathVariable Long tradeId) {
        TradeResult result = tradeService.confirmPayment(tradeId);
        return ResponseEntity.ok(TradeResponseMapper.from(result));
    }

    @PostMapping("/trades/{tradeId}/confirm-received")
    public ResponseEntity<TradeResponse> confirmReceived(@PathVariable Long tradeId) {
        TradeResult result = tradeService.confirmReceived(tradeId);
        return ResponseEntity.ok(TradeResponseMapper.from(result));
    }


     @GetMapping("/orders/buyers")
    public ResponseEntity<List<OrderResponse>> getBuyOrders(
            @RequestParam(required = false) String token,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) String sortByPrice
    ) {
        List<OrderResult> orders = orderService.getOrders("BUY", token, paymentMethod, sortByPrice);
        return ResponseEntity.ok(OrderResponseMapper.fromList(orders));
    }

    /**
     * Danh sách người bán (SELL orders)
     */
    @GetMapping("/orders/sellers")
    public ResponseEntity<List<OrderResponse>> getSellOrders(
            @RequestParam(required = false) String token,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) String sortByPrice
    ) {
        List<OrderResult> orders = orderService.getOrders("SELL", token, paymentMethod, sortByPrice);
        return ResponseEntity.ok(OrderResponseMapper.fromList(orders));
    }


    @PostMapping("/trades/{tradeId}/cancel")
    public ResponseEntity<TradeResponse> cancelTrade(@PathVariable Long tradeId) {

    TradeResult result = tradeService.cancelTrade(tradeId);
    return ResponseEntity.ok(TradeResponseMapper.from(result));
    }
   
}
