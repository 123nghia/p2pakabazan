package com.akabazan.api.controller;

import com.akabazan.api.dto.IntegrationUserResponse;
import com.akabazan.api.mapper.IntegrationMapper;
import com.akabazan.api.request.IntegrationUserRequest;
import com.akabazan.service.ExternalUserIntegrationService;
import com.akabazan.service.dto.IntegrationSyncCommand;
import com.akabazan.service.dto.IntegrationSyncResult;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/integration")
public class IntegrationController {

    private final ExternalUserIntegrationService integrationService;

    public IntegrationController(ExternalUserIntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    @PostMapping("/users/sync")
    public ResponseEntity<IntegrationUserResponse> syncUser(@Valid @RequestBody IntegrationUserRequest request) {
        IntegrationSyncCommand command = IntegrationMapper.toCommand(request);
        IntegrationSyncResult result = integrationService.syncUserAndWallet(command);
        IntegrationUserResponse response = IntegrationMapper.toResponse(result);

        return ResponseEntity.ok(response);
    }
}
