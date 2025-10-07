package com.akabazan.api.controller;

import com.akabazan.api.mapper.OrderResponseMapper;
import com.akabazan.api.reponse.OrderResponse;
import com.akabazan.api.request.OrderQueryRequest;
import com.akabazan.common.dto.BaseResponse;
import com.akabazan.common.dto.ResponseFactory;
import com.akabazan.service.MarketService;
import com.akabazan.service.OrderService;
import com.akabazan.service.dto.OrderResult;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/market")
@CrossOrigin(origins = {
    "http://localhost:5500",
    "http://localhost:5174"
})

public class MarketController extends BaseController {
    private final MarketService marketService;
    private final OrderService orderService;

    public MarketController(MarketService marketService, OrderService orderService) {
        this.marketService = marketService;
        this.orderService = orderService;
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
        Page<OrderResult> orders = orderService.getOrders(
                request.getType(),
                request.getToken(),
                request.getPaymentMethod(),
                request.getSortByPrice(),
                request.getPageOrDefault(),
                request.getSizeOrDefault());
        
        return ResponseEntity.ok(buildPagedResponse(orders, OrderResponseMapper::from));
    }
}
