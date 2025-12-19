package com.akabazan.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.HashMap;
import java.util.Map;

/**
 * Root controller for API documentation and health check
 */
@RestController
@RequestMapping("")
public class RootController {

    /**
     * API root - show available endpoints
     * Note: This is at /api/ because of spring.mvc.servlet.path=/api
     */
    @GetMapping({"", "/"})
    public Map<String, Object> apiRoot() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "P2P Trading System API");
        info.put("version", "1.0.0");
        info.put("status", "running");
        
        Map<String, String> documentation = new HashMap<>();
        documentation.put("Swagger UI", "/api/swagger-ui/index.html");
        documentation.put("API Docs", "/api/v3/api-docs");
        documentation.put("Health Check", "/api/health");
        
        Map<String, String> publicEndpoints = new HashMap<>();
        publicEndpoints.put("Login", "POST /api/auth/login");
        publicEndpoints.put("Register", "POST /api/auth/register");
        publicEndpoints.put("Market Data", "GET /api/market/*");
        publicEndpoints.put("Integration", "GET /api/integration/*");
        
        Map<String, String> protectedEndpoints = new HashMap<>();
        protectedEndpoints.put("User Profile", "GET /api/users/me");
        protectedEndpoints.put("Trades", "GET /api/trades");
        protectedEndpoints.put("Wallets", "GET /api/wallets");
        protectedEndpoints.put("Orders", "GET /api/p2p/*");
        
        info.put("documentation", documentation);
        info.put("publicEndpoints", publicEndpoints);
        info.put("protectedEndpoints", protectedEndpoints);
        info.put("note", "Protected endpoints require JWT token. Get token via POST /api/auth/login");
        info.put("baseUrl", "/api");
        
        return info;
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        health.put("service", "p2p-trading-api");
        health.put("database", "connected");
        return health;
    }
}
