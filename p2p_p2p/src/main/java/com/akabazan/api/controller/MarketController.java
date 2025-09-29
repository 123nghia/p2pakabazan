package com.akabazan.api.controller;

import com.akabazan.api.request.LoginRequest;
import com.akabazan.service.AuthService;
import com.akabazan.service.MarketService;
import com.fasterxml.jackson.databind.JsonNode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/market")
@CrossOrigin(origins = "http://localhost:5500") // Cho phép FE gọi
public class MarketController {
    private final MarketService marketService;

    public MarketController(MarketService marketService) {
        this.marketService = marketService;
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
}
 

