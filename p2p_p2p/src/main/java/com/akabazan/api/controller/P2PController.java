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
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
@RestController
@RequestMapping("/p2p")

public class P2PController extends BaseController {

    private final OrderService orderService;

    public P2PController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/orders")
    public ResponseEntity<BaseResponse<OrderResponse>> createOrder(@Valid @RequestBody OrderRequest req) {
            
            OrderResult result = orderService.createOrder(OrderCommandMapper.toCommand(req));
            return ResponseFactory.ok(OrderResponseMapper.from(result));
    }


    @GetMapping("/orders/me")
    public ResponseEntity<BaseResponse<MyOrdersResponse>> getMyOrders(@ModelAttribute OrderUserRequest req) {
        List<OrderResult> results = orderService.getOrdersByUserToken(req.getToken(), req.getStatus(), req.getType());
        MyOrdersResponse payload = buildMyOrdersResponse(results);
        return ResponseFactory.ok(payload);
    }

    private MyOrdersResponse buildMyOrdersResponse(List<OrderResult> results) {
        List<OrderResponse> orders = OrderResponseMapper.fromList(results);
        long totalTrades = calculateTotalTrades(results);
        double completionRate = calculateCompletionRate(results);

        MyOrdersResponse payload = new MyOrdersResponse();
        payload.setOrders(orders);
        payload.setTotalTrades(totalTrades);
        payload.setCompletionRate(completionRate);
        return payload;
    }

    private long calculateTotalTrades(List<OrderResult> results) {
        return results.stream()
                .mapToLong(OrderResult::getTradeCount)
                .sum();
    }

    private double calculateCompletionRate(List<OrderResult> results) {
        if (results.isEmpty()) {
            return 0.0;
        }
        long totalTrades = results.stream()
                .mapToLong(OrderResult::getTradeCount)
                .sum();
        if (totalTrades == 0L) {
            return 0.0;
        }
        long completedTrades = results.stream()
                .mapToLong(OrderResult::getCompletedTradeCount)
                .sum();
        double completionRateRaw = (completedTrades * 100.0) / totalTrades;
        return BigDecimal.valueOf(completionRateRaw)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable UUID orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok().build();
    }
}
