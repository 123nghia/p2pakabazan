package com.akabazan.api.controller;
import com.akabazan.api.dto.UserDTO;
import com.akabazan.api.mapper.UserMapper;
import com.akabazan.service.CurrentUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final CurrentUserService currentUserService;

       public UserController(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        return currentUserService.getCurrentUser()
                .map(UserMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401).build());
    }
}