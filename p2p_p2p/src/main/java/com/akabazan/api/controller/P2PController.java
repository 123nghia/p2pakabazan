package com.akabazan.api.controller;

import com.akabazan.api.mapper.OrderMapper;
import com.akabazan.api.mapper.TradeMapper;
import com.akabazan.api.request.OrderRequest;
import com.akabazan.api.request.TradeRequest;
import com.akabazan.service.OrderService;
import com.akabazan.service.TradeService;
import com.akabazan.service.dto.OrderDTO;
import com.akabazan.service.dto.TradeDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/p2p")
@CrossOrigin(origins = "http://localhost:5500") // Cho phép FE gọi

public class P2PController {
  
    private final OrderService orderService;
    private final TradeService tradeService;

    public P2PController(OrderService orderService, TradeService tradeService) {
        this.orderService = orderService;
        this.tradeService = tradeService;
    }

    // ====================== ORDER ======================

    @PostMapping("/orders")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderRequest orderRequest) {
    // Map từ OrderRequest → OrderDTO
    OrderDTO orderDTO = OrderMapper.toDTO(orderRequest);
    // Gọi service tạo order
    OrderDTO result = orderService.createOrder(orderDTO);
    return ResponseEntity.ok(result);
    }



    @GetMapping("/orders")
    public ResponseEntity<List<OrderDTO>> getOrders(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String token,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) String sortByPrice
    ) {
        List<OrderDTO> orders = orderService.getOrders(type, token, paymentMethod, sortByPrice);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/orders/{orderId}/trades")
    public ResponseEntity<List<TradeDTO>> getTradesByOrder(@PathVariable Long orderId) {
        List<TradeDTO> trades = tradeService.getTradesByOrder(orderId);
        return ResponseEntity.ok(trades);
    }

    /**
     * Người bán hủy order (chỉ khi chưa có trade).
     */
    
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok().build();
    }

    // ====================== TRADE ======================

    @PostMapping("/trades")
    public ResponseEntity<TradeDTO> createTrade(@RequestBody TradeRequest tradeDTO) {
        TradeDTO dto = TradeMapper.toDTO(tradeDTO); // map từ API request sang DTO
        TradeDTO result = tradeService.createTrade(dto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/trades/{tradeId}/confirm-payment")
    public ResponseEntity<TradeDTO> confirmPayment(@PathVariable Long tradeId) {
        TradeDTO result = tradeService.confirmPayment(tradeId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/trades/{tradeId}/confirm-received")
    public ResponseEntity<TradeDTO> confirmReceived(@PathVariable Long tradeId) {
        TradeDTO result = tradeService.confirmReceived(tradeId);
        return ResponseEntity.ok(result);
    }


     @GetMapping("/orders/buyers")
    public ResponseEntity<List<OrderDTO>> getBuyOrders(
            @RequestParam(required = false) String token,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) String sortByPrice
    ) {
        List<OrderDTO> orders = orderService.getOrders("BUY", token, paymentMethod, sortByPrice);
        return ResponseEntity.ok(orders);
    }

    /**
     * Danh sách người bán (SELL orders)
     */
    @GetMapping("/orders/sellers")
    public ResponseEntity<List<OrderDTO>> getSellOrders(
            @RequestParam(required = false) String token,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) String sortByPrice
    ) {
        List<OrderDTO> orders = orderService.getOrders("SELL", token, paymentMethod, sortByPrice);
        return ResponseEntity.ok(orders);
    }


    @PostMapping("/trades/{tradeId}/cancel")
    public ResponseEntity<TradeDTO> cancelTrade(@PathVariable Long tradeId) {

    TradeDTO result = tradeService.cancelTrade(tradeId);
    return ResponseEntity.ok(result);
    }


       

   
}
