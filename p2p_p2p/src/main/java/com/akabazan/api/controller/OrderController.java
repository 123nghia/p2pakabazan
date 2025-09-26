package com.akabazan.api.controller;

import com.akabazan.service.dto.*;
import com.akabazan.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Tạo order mới
     */
    @PostMapping("/create")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok(orderService.createOrder(orderDTO));
    }

    /**
     * Lấy danh sách order đang mở
     */
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getOrders(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String token,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) String sortByPrice
    ) {
        return ResponseEntity.ok(orderService.getOrders(type, token, paymentMethod, sortByPrice));
    }

    /**
     * Mua/bán tạo trade từ order
     */
    @PostMapping("/trade")
    public ResponseEntity<TradeDTO> createTrade(@RequestBody TradeDTO tradeDTO) {
        return ResponseEntity.ok(orderService.createTrade(tradeDTO));
    }

    /**
     * Người mua xác nhận đã thanh toán
     */
    @PostMapping("/trade/{tradeId}/confirm-payment")
    public ResponseEntity<TradeDTO> confirmPayment(@PathVariable Long tradeId) {
        return ResponseEntity.ok(orderService.confirmPayment(tradeId));
    }

    /**
     * Người bán xác nhận đã nhận tiền
     */
    @PostMapping("/trade/{tradeId}/confirm-received")
    public ResponseEntity<TradeDTO> confirmReceived(@PathVariable Long tradeId) {
        return ResponseEntity.ok(orderService.confirmReceived(tradeId));
    }

    /**
     * Gửi tin nhắn chat trong trade
     */
    @PostMapping("/trade/{tradeId}/chat")
    public ResponseEntity<TradeDTO> sendChatMessage(
            @PathVariable Long tradeId,
            @RequestBody ChatMessageDTO messageDTO
    ) {
        return ResponseEntity.ok(orderService.sendChatMessage(tradeId, messageDTO));
    }

    /**
     * Mở tranh chấp (dispute)
     */
    @PostMapping("/trade/{tradeId}/dispute")
    public ResponseEntity<TradeDTO> openDispute(
            @PathVariable Long tradeId,
            @RequestParam String reason,
            @RequestParam String evidence
    ) {
        return ResponseEntity.ok(orderService.openDispute(tradeId, reason, evidence));
    }
}