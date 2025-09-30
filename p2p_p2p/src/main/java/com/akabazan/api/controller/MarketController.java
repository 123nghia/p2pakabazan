package com.akabazan.api.controller;

import com.akabazan.service.MarketService;
import com.akabazan.service.OrderService;
import com.akabazan.service.dto.OrderDTO;
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
            @RequestParam String token,
            @RequestParam String fiat,
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
    public ResponseEntity<List<OrderDTO>> getPublicBuyOrders(
            @RequestParam(required = false) String token,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) String sortByPrice) {
        List<OrderDTO> orders = orderService.getOrders("BUY", token, paymentMethod, sortByPrice);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/orders/sell")
    public ResponseEntity<List<OrderDTO>> getPublicSellOrders(
            @RequestParam(required = false) String token,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) String sortByPrice) {
        List<OrderDTO> orders = orderService.getOrders("SELL", token, paymentMethod, sortByPrice);
        return ResponseEntity.ok(orders);
    }
}
