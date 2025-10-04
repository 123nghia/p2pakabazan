package com.akabazan.api.controller;
import com.akabazan.api.reponse.AuthResponse;
import com.akabazan.api.reponse.OrderResponse;
import com.akabazan.api.request.LoginRequest;
import com.akabazan.api.request.RegisterRequest;
import com.akabazan.common.dto.BaseResponse;
import com.akabazan.common.dto.ResponseFactory;
import com.akabazan.service.AuthService;
import com.akabazan.service.dto.AuthResult;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {
    "http://localhost:5500",
    "http://localhost:5174"

})

public class AuthController extends BaseController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public   ResponseEntity<BaseResponse<AuthResponse>>  login(@Valid @RequestBody LoginRequest request) {
        AuthResult result = authService.login(request.getEmail(), request.getPassword());
        return ResponseFactory.ok(toResponse(result));
    }

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResult result = authService.register(request.getEmail(), request.getPassword());
        return  ResponseFactory.ok(toResponse(result));
    }

    private AuthResponse toResponse(AuthResult result) {
        return AuthResponse.from(result.getToken(), result.getUserId(), result.getEmail());
    }
}
