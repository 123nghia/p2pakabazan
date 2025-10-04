package com.akabazan.api.controller;
import com.akabazan.api.mapper.UserMapper;
import com.akabazan.api.mapper.UserTradesOrdersResponseMapper;
import com.akabazan.api.reponse.UserResponse;
import com.akabazan.api.reponse.UserTradesOrdersResponse;
import com.akabazan.common.dto.BaseResponse;
import com.akabazan.common.dto.ResponseFactory;
import com.akabazan.service.CurrentUserService;
import com.akabazan.service.UserTradeOrderService;
import com.akabazan.service.dto.UserTradesOrdersResult;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = {
    "http://localhost:5500",
   "http://localhost:5174"
})

public class UserController extends BaseController {

    private final CurrentUserService currentUserService;

    private final UserTradeOrderService userTradeOrderService;
    
    public UserController(CurrentUserService currentUserService, 
    UserTradeOrderService  userTradeOrderService
    ) {
        this.currentUserService = currentUserService;
        this.userTradeOrderService = userTradeOrderService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        return currentUserService.getCurrentUser()
                .map(UserMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401).build());
    }

    @GetMapping("/my-activities")
    public ResponseEntity<BaseResponse<UserTradesOrdersResponse>> getMyTradesAndOrders() {
        var userId = currentUserService.getCurrentUserId().get();
        UserTradesOrdersResult result = userTradeOrderService.getUserTradesAndOrders(userId);
        return ResponseFactory.ok(UserTradesOrdersResponseMapper.from(result));
    }
}
