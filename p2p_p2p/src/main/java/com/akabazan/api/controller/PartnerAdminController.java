package com.akabazan.api.controller;

import com.akabazan.api.reponse.PartnerCredentialResponse;
import com.akabazan.api.request.CreatePartnerRequest;
import com.akabazan.common.dto.BaseResponse;
import com.akabazan.common.dto.ResponseFactory;
import com.akabazan.service.PartnerSsoClientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/integration/partners")
public class PartnerAdminController extends BaseController {

    private final PartnerSsoClientService partnerSsoClientService;

    @Value("${app.sso.admin-token:}")
    private String adminToken;

    public PartnerAdminController(PartnerSsoClientService partnerSsoClientService) {
        this.partnerSsoClientService = partnerSsoClientService;
    }

    @PostMapping
    public ResponseEntity<BaseResponse<PartnerCredentialResponse>> createPartner(
            @RequestHeader(value = "X-Admin-Token", required = false) String providedToken,
            @Valid @RequestBody CreatePartnerRequest request) {

        if (adminToken == null || adminToken.isBlank()) {
            return ResponseFactory.error(404, "Admin endpoint disabled");
        }
        if (providedToken == null || providedToken.isBlank() || !adminToken.trim().equals(providedToken.trim())) {
            return ResponseFactory.error(401, "Unauthorized");
        }

        try {
            PartnerSsoClientService.PartnerCredentials credentials =
                    partnerSsoClientService.createPartner(request.getPartnerId());
            return ResponseFactory.ok(new PartnerCredentialResponse(credentials.partnerId(), credentials.sharedSecret()));
        } catch (IllegalStateException e) {
            return ResponseFactory.error(409, e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseFactory.error(400, e.getMessage());
        }
    }
}

