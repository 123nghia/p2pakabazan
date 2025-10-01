package com.akabazan.api.controller;

import com.akabazan.api.dto.OrderResponse;
import com.akabazan.api.mapper.OrderResponseMapper;
import com.akabazan.service.MarketService;
import com.akabazan.service.OrderService;
import com.akabazan.service.dto.OrderResult;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/market")
@CrossOrigin(origins = "http://localhost:5500") // Cho phép FE gọi
public class MarketController {
    private final MarketService marketService;
    private final OrderService orderService;

    public MarketController(MarketService marketService, OrderService orderService) {
        this.marketService = marketService;
        this.orderService = orderService;
    }

    @GetMapping("/price")
    public ResponseEntity<Double> getPrice(
            @RequestParam(defaultValue = "USDT") String token,
            @RequestParam(defaultValue = "VND") String fiat,
            @RequestParam(defaultValue = "SELL") String tradeType,
            @RequestParam(defaultValue = "5") int top) {

        try {
            Double price = marketService.getP2PPrice(token, fiat, tradeType, top);
            return ResponseEntity.ok(price);  // trả Double trực tiếp
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/orders/buy")
    public ResponseEntity<List<OrderResponse>> getPublicBuyOrders(
            @RequestParam(required = false) String token,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) String sortByPrice) {
        List<OrderResult> orders = orderService.getOrders("BUY", token, paymentMethod, sortByPrice);
        return ResponseEntity.ok(OrderResponseMapper.fromList(orders));
    }

    @GetMapping("/orders/sell")
    public ResponseEntity<List<OrderResponse>> getPublicSellOrders(
            @RequestParam(required = false) String token,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) String sortByPrice) {
        List<OrderResult> orders = orderService.getOrders("SELL", token, paymentMethod, sortByPrice);
        return ResponseEntity.ok(OrderResponseMapper.fromList(orders));
    }
}
