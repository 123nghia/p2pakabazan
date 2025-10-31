package com.akabazan.api.controller;

import com.akabazan.api.mapper.OrderResponseMapper;
import com.akabazan.api.reponse.OrderResponse;
import com.akabazan.api.request.OrderQueryRequest;
import com.akabazan.common.dto.BaseResponse;
import com.akabazan.common.dto.ResponseFactory;
import com.akabazan.service.CurrentUserService;
import com.akabazan.service.MarketService;
import com.akabazan.service.OrderService;
import com.akabazan.service.dto.OrderResult;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/market")

public class MarketController extends BaseController {
    private final MarketService marketService;
    private final OrderService orderService;
    private final CurrentUserService currentUserService;

    public MarketController(MarketService marketService, OrderService orderService, CurrentUserService currentUserService) {
        this.marketService = marketService;
        this.orderService = orderService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/price")
    public ResponseEntity<BaseResponse<Double>> getPrice(
            @RequestParam(defaultValue = "USDT") String token,
            @RequestParam(defaultValue = "VND") String fiat,
            @RequestParam(defaultValue = "SELL") String tradeType,
            @RequestParam(defaultValue = "5") int top) throws Exception 
     {

        Double price = marketService.getP2PPrice(token, fiat, tradeType, top);
        return ResponseFactory.ok(price);
    }

    @GetMapping("/orders")
    public ResponseEntity<BaseResponse<List<OrderResponse>>> getPublicBuyOrders(@ModelAttribute OrderQueryRequest request) { 

        List<String> paymentMethods = request.getPaymentMethods();
        boolean allPayments = paymentMethods.stream()
                .anyMatch(pm -> "ALL-PAYMENTS".equalsIgnoreCase(pm));
        List<String> normalizedPaymentMethods = allPayments ? null : (!paymentMethods.isEmpty() ? paymentMethods : null);

        UUID currentUserId = currentUserService.getCurrentUserId().orElse(null);

        Page<OrderResult> orders = orderService.getOrders(
                request.getType(),
                request.getToken(),
                normalizedPaymentMethods,
                request.getSortByPrice(),
                request.getFiat(),
                currentUserId,
                request.getPageOrDefault(),
                request.getSizeOrDefault());
        
        return ResponseEntity.ok(buildPagedResponse(orders, OrderResponseMapper::from));
    }
}
