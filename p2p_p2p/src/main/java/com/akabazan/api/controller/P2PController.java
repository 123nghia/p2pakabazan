package com.akabazan.api.controller;

import com.akabazan.api.mapper.OrderCommandMapper;
import com.akabazan.api.mapper.OrderResponseMapper;
import com.akabazan.api.reponse.OrderResponse;
import com.akabazan.api.request.OrderQueryRequest;
import com.akabazan.api.request.OrderRequest;
import com.akabazan.common.dto.BaseResponse;
import com.akabazan.common.dto.ResponseFactory;
import com.akabazan.service.OrderService;
import com.akabazan.service.dto.OrderResult;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/p2p")
@CrossOrigin(origins = {
        "http://localhost:5500",
        "http://localhost:5174"
})

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

    @GetMapping("/orders")
    public ResponseEntity<BaseResponse<List<OrderResponse>>> getOrders(@ModelAttribute OrderQueryRequest request) {
       
            return ResponseFactory.paged(
             orderService.getOrders(
                        request.getType(),
                        request.getToken(),
                        request.getPaymentMethod(),
                        request.getSortByPrice(),
                        request.getPageOrDefault(),
                        request.getSizeOrDefault()
            ),
            OrderResponseMapper::from
    );
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok().build();
    }
    

}
