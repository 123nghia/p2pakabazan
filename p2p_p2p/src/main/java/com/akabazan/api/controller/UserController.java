package com.akabazan.api.controller;
import com.akabazan.api.dto.UserDTO;
import com.akabazan.api.mapper.UserMapper;
import com.akabazan.service.CurrentUserService;
import com.akabazan.service.UserTradeOrderService;
import com.akabazan.service.dto.UserTradesOrdersDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final CurrentUserService currentUserService;

    private final UserTradeOrderService userTradeOrderService;
    
    public UserController(CurrentUserService currentUserService, 
    UserTradeOrderService  userTradeOrderService
    ) {
        this.currentUserService = currentUserService;
        this.userTradeOrderService = userTradeOrderService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        return currentUserService.getCurrentUser()
                .map(UserMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401).build());
    }

    @GetMapping("/my-activities")
    public ResponseEntity<UserTradesOrdersDTO> getMyTradesAndOrders() {
        var userId = currentUserService.getCurrentUserId().get();
        UserTradesOrdersDTO result = userTradeOrderService.getUserTradesAndOrders(userId);
        return ResponseEntity.ok(result);
    }
}