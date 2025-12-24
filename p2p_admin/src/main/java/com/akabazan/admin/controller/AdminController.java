package com.akabazan.admin.controller;

import com.akabazan.common.dto.BaseResponse;
import com.akabazan.common.dto.ResponseFactory;
import com.akabazan.repository.OrderRepository;
import com.akabazan.repository.TradeRepository;
import com.akabazan.repository.UserRepository;
import com.akabazan.repository.entity.User;
import com.akabazan.service.dto.OrderMapper;
import com.akabazan.service.dto.OrderResult;
import com.akabazan.service.dto.TradeResult;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final TradeRepository tradeRepository;
    private final OrderRepository orderRepository;
    private final com.akabazan.repository.CurrencyRepository currencyRepository;
    private final com.akabazan.repository.PaymentMethodRepository paymentMethodRepository;

    public AdminController(UserRepository userRepository,
            TradeRepository tradeRepository,
            OrderRepository orderRepository,
            com.akabazan.repository.CurrencyRepository currencyRepository,
            com.akabazan.repository.PaymentMethodRepository paymentMethodRepository) {
        this.userRepository = userRepository;
        this.tradeRepository = tradeRepository;
        this.orderRepository = orderRepository;
        this.currencyRepository = currencyRepository;
        this.paymentMethodRepository = paymentMethodRepository;
    }

    @GetMapping("/currencies")
    public ResponseEntity<BaseResponse<List<com.akabazan.repository.entity.Currency>>> listCurrencies() {
        return ResponseFactory.ok(currencyRepository.findAll());
    }

    @PostMapping("/currencies")
    public ResponseEntity<BaseResponse<com.akabazan.repository.entity.Currency>> saveCurrency(
            @RequestBody com.akabazan.repository.entity.Currency currency) {
        if (currency.getId() != null) {
            com.akabazan.repository.entity.Currency existing = currencyRepository.findById(currency.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid currency Id:" + currency.getId()));
            existing.setType(currency.getType());
            existing.setCode(currency.getCode());
            existing.setName(currency.getName());
            existing.setNetwork(currency.getNetwork());
            existing.setIconUrl(currency.getIconUrl());
            existing.setDecimalPlaces(currency.getDecimalPlaces());
            existing.setDisplayOrder(currency.getDisplayOrder());
            existing.setActive(currency.isActive());
            existing.setUpdatedAt(java.time.LocalDateTime.now());
            return ResponseFactory.ok(currencyRepository.save(existing));
        }
        return ResponseFactory.ok(currencyRepository.save(currency));
    }

    @GetMapping("/payment-methods")
    public ResponseEntity<BaseResponse<List<com.akabazan.repository.entity.PaymentMethod>>> listPaymentMethods() {
        return ResponseFactory.ok(paymentMethodRepository.findAll());
    }

    @PostMapping("/payment-methods")
    public ResponseEntity<BaseResponse<com.akabazan.repository.entity.PaymentMethod>> savePaymentMethod(
            @RequestBody com.akabazan.repository.entity.PaymentMethod paymentMethod) {
        if (paymentMethod.getId() != null) {
            com.akabazan.repository.entity.PaymentMethod existing = paymentMethodRepository
                    .findById(paymentMethod.getId())
                    .orElseThrow(
                            () -> new IllegalArgumentException("Invalid payment method Id:" + paymentMethod.getId()));
            existing.setType(paymentMethod.getType());
            existing.setCode(paymentMethod.getCode());
            existing.setName(paymentMethod.getName());
            existing.setDescription(paymentMethod.getDescription());
            existing.setIconUrl(paymentMethod.getIconUrl());
            existing.setDisplayOrder(paymentMethod.getDisplayOrder());
            existing.setActive(paymentMethod.isActive());
            existing.setUpdatedAt(java.time.LocalDateTime.now());
            return ResponseFactory.ok(paymentMethodRepository.save(existing));
        }
        return ResponseFactory.ok(paymentMethodRepository.save(paymentMethod));
    }

    @GetMapping("/users")
    public ResponseEntity<BaseResponse<List<User>>> listUsers() {
        List<User> users = userRepository.findAll();
        return ResponseFactory.ok(users);
    }

    @GetMapping("/trades")
    public ResponseEntity<BaseResponse<List<TradeResult>>> listTrades(
            @RequestParam(value = "status", required = false) String status) {
        List<TradeResult> trades = tradeRepository.findAll().stream()
                .filter(t -> status == null || t.getStatus().name().equalsIgnoreCase(status))
                .map(com.akabazan.service.dto.TradeMapper::toResult)
                .collect(Collectors.toList());
        return ResponseFactory.ok(trades);
    }

    @GetMapping("/users/{userId}/trades")
    public ResponseEntity<BaseResponse<List<TradeResult>>> listTradesOfUser(@PathVariable UUID userId) {
        List<TradeResult> trades = tradeRepository.findByUser(userId).stream()
                .map(com.akabazan.service.dto.TradeMapper::toResult)
                .collect(Collectors.toList());
        return ResponseFactory.ok(trades);
    }

    @GetMapping("/orders")
    public ResponseEntity<BaseResponse<List<OrderResult>>> listOrders(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "token", required = false) String token) {
        var orders = orderRepository.findAll().stream()
                .filter(o -> status == null || (o.getStatus() != null && o.getStatus().equalsIgnoreCase(status)))
                .filter(o -> type == null || (o.getType() != null && o.getType().equalsIgnoreCase(type)))
                .filter(o -> token == null || (o.getToken() != null && o.getToken().equalsIgnoreCase(token)))
                .map(o -> OrderMapper.toResult(o, List.of()))
                .collect(Collectors.toList());
        return ResponseFactory.ok(orders);
    }

    @GetMapping("/users/{userId}/orders")
    public ResponseEntity<BaseResponse<List<OrderResult>>> listOrdersOfUser(@PathVariable UUID userId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "type", required = false) String type) {
        var orders = orderRepository.findOrdersByUserAndOptionalFilters(userId, status, type).stream()
                .map(o -> OrderMapper.toResult(o, List.of()))
                .collect(Collectors.toList());
        return ResponseFactory.ok(orders);
    }
}
