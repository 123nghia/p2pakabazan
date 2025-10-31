package com.akabazan.admin.controller;

import com.akabazan.admin.security.UserAdminRepository;
import com.akabazan.common.dto.BaseResponse;
import com.akabazan.common.dto.ResponseFactory;
import com.akabazan.repository.OrderRepository;
import com.akabazan.repository.TradeRepository;
import com.akabazan.repository.UserRepository;
import com.akabazan.repository.constant.TradeStatus;
import com.akabazan.repository.entity.User;
import com.akabazan.service.DisputeService;
import com.akabazan.service.TradeService;
import com.akabazan.service.dto.DisputeResult;
import com.akabazan.service.dto.OrderMapper;
import com.akabazan.service.dto.OrderResult;
import com.akabazan.service.dto.TradeResult;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final TradeRepository tradeRepository;
    private final OrderRepository orderRepository;
    private final TradeService tradeService;
    private final DisputeService disputeService;
    private final UserAdminRepository userAdminRepository;

    public AdminController(UserRepository userRepository,
                           TradeRepository tradeRepository,
                           OrderRepository orderRepository,
                           TradeService tradeService,
                           DisputeService disputeService,
                           UserAdminRepository userAdminRepository) {
        this.userRepository = userRepository;
        this.tradeRepository = tradeRepository;
        this.orderRepository = orderRepository;
        this.tradeService = tradeService;
        this.disputeService = disputeService;
        this.userAdminRepository = userAdminRepository;
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

    @GetMapping("/disputes")
    public ResponseEntity<BaseResponse<List<DisputeResult>>> listDisputes(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "onlyMine", required = false, defaultValue = "false") boolean onlyMine) {
        com.akabazan.repository.entity.Dispute.DisputeStatus disputeStatus = null;
        if (status != null && !status.isBlank()) {
            disputeStatus = com.akabazan.repository.entity.Dispute.DisputeStatus.valueOf(status);
        }
        List<DisputeResult> disputes = disputeService.getDisputes(disputeStatus, onlyMine);
        return ResponseFactory.ok(disputes);
    }

    @PostMapping("/disputes/{disputeId}/resolve")
    public ResponseEntity<BaseResponse<DisputeResult>> resolveDispute(
            @PathVariable UUID disputeId,
            @RequestParam("outcome") String outcome,
            @RequestParam(value = "note", required = false) String note) {
        DisputeResult result = disputeService.resolveDispute(disputeId, outcome, note);
        return ResponseFactory.ok(result);
    }

    @PostMapping("/disputes/{disputeId}/reject")
    public ResponseEntity<BaseResponse<DisputeResult>> rejectDispute(
            @PathVariable UUID disputeId,
            @RequestParam(value = "note", required = false) String note) {
        DisputeResult result = disputeService.rejectDispute(disputeId, note);
        return ResponseFactory.ok(result);
    }

    @PostMapping("/disputes/{disputeId}/assign")
    public ResponseEntity<BaseResponse<DisputeResult>> assignDispute(
            @PathVariable UUID disputeId,
            @RequestParam(value = "adminId", required = false) UUID adminId) {
        DisputeResult result;
        if (adminId == null) {
            // Lấy admin hiện tại từ session (username) → user_admin.id
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            UUID currentAdminId = userAdminRepository.findByUsername(username)
                    .map(user -> user.getId())
                    .orElseThrow(() -> new com.akabazan.common.exception.ApplicationException(com.akabazan.common.constant.ErrorCode.USER_NOT_FOUND));
            result = disputeService.assignToAdmin(disputeId, currentAdminId);
        } else {
            result = disputeService.assignToAdmin(disputeId, adminId);
        }
        return ResponseFactory.ok(result);
    }
}


