package com.akabazan.api.controller;

import com.akabazan.api.mapper.OrderCommandMapper;
import com.akabazan.api.mapper.OrderResponseMapper;
import com.akabazan.api.reponse.MyOrdersResponse;
import com.akabazan.api.reponse.OrderResponse;
import com.akabazan.api.request.OrderRequest;
import com.akabazan.api.request.OrderUserRequest;
import com.akabazan.common.dto.BaseResponse;
import com.akabazan.common.dto.ResponseFactory;
import com.akabazan.service.OrderService;
import com.akabazan.service.dto.OrderResult;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/p2p")

public class P2PController extends BaseController {

    private final OrderService orderService;

    public P2PController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/orders")
    public ResponseEntity<BaseResponse<OrderResponse>> createOrder(@RequestBody OrderRequest req) {
            
            OrderResult result = orderService.createOrder(OrderCommandMapper.toCommand(req));
            return ResponseFactory.ok(OrderResponseMapper.from(result));
    }


    @GetMapping("/orders/me")
    public ResponseEntity<BaseResponse<MyOrdersResponse>> getMyOrders(   
            @ModelAttribute  OrderUserRequest  req
    ) {
        List<OrderResult> results = orderService.getOrdersByUserToken(req.gettoken(), req.getStatus(), req.getType());
        List<OrderResponse> orders = OrderResponseMapper.fromList(results);
        long totalTrades = results.isEmpty() ? 0L : results.get(0).getTradeCount();
        double completionRateRaw = results.isEmpty() ? 0.0 : results.get(0).getCompletionRate();
        double completionRate = BigDecimal.valueOf(completionRateRaw)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();

        MyOrdersResponse payload = new MyOrdersResponse();
        payload.setOrders(orders);
        payload.setTotalTrades(totalTrades);
        payload.setCompletionRate(completionRate);
        return ResponseFactory.ok(payload);
    }


    
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok().build();
    }
    

}
