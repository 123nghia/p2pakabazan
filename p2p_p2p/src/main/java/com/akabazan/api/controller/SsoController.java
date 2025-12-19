package com.akabazan.api.controller;

import com.akabazan.api.sso.dto.SsoIssueRequest;
import com.akabazan.api.sso.dto.SsoIssueResponse;
import com.akabazan.api.sso.dto.SsoExchangeRequest;
import com.akabazan.api.sso.dto.SsoExchangeResponse;
import com.akabazan.api.sso.security.PartnerAuthResult;
import com.akabazan.api.sso.security.PartnerHmacAuthenticator;
import com.akabazan.api.sso.store.RedisSsoCodeStore;
import com.akabazan.api.sso.util.SsoUserIdentity;
import com.akabazan.common.dto.BaseResponse;
import com.akabazan.common.dto.ResponseFactory;
import com.akabazan.repository.entity.User;
import com.akabazan.service.AuthService;
import com.akabazan.service.PartnerUserProvisioningService;
import com.akabazan.service.dto.PartnerUserProvisioningCommand;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;

@RestController
@RequestMapping("/sso")
public class SsoController extends BaseController {

    private final PartnerHmacAuthenticator partnerHmacAuthenticator;
    private final PartnerUserProvisioningService partnerUserProvisioningService;
    private final AuthService authService;
    private final RedisSsoCodeStore redisSsoCodeStore;

    public SsoController(PartnerHmacAuthenticator partnerHmacAuthenticator,
                         PartnerUserProvisioningService partnerUserProvisioningService,
                         AuthService authService,
                         RedisSsoCodeStore redisSsoCodeStore) {
        this.partnerHmacAuthenticator = partnerHmacAuthenticator;
        this.partnerUserProvisioningService = partnerUserProvisioningService;
        this.authService = authService;
        this.redisSsoCodeStore = redisSsoCodeStore;
    }

    @PostMapping("/issue")
    public ResponseEntity<BaseResponse<SsoIssueResponse>> issue(HttpServletRequest request,
                                                                @Valid @RequestBody SsoIssueRequest body) {
        String rawBody = readCachedRequestBody(request);
        PartnerAuthResult auth = partnerHmacAuthenticator.authenticate(request, rawBody);
        if (!auth.isAuthenticated()) {
            return ResponseFactory.error(401, auth.getErrorMessage() == null ? "Unauthorized" : auth.getErrorMessage());
        }

        String fallbackEmail = SsoUserIdentity.toSyntheticEmail(auth.getPartnerId(), body.getExternalUserId());
        User.KycStatus kycStatus = null;
        if (body.getKycStatus() != null && !body.getKycStatus().isBlank()) {
            kycStatus = User.KycStatus.valueOf(body.getKycStatus().trim().toUpperCase(Locale.ROOT));
        }
        PartnerUserProvisioningCommand command = new PartnerUserProvisioningCommand(
                auth.getPartnerId(),
                body.getExternalUserId(),
                fallbackEmail,
                body.getEmail(),
                body.getUsername(),
                kycStatus);

        var userId = partnerUserProvisioningService.provisionPartnerUser(command);
        String code = redisSsoCodeStore.issueCode(userId);

        return ResponseFactory.ok(new SsoIssueResponse(code, redisSsoCodeStore.getCodeTtlSeconds()));
    }

    @PostMapping("/exchange")
    public ResponseEntity<BaseResponse<SsoExchangeResponse>> exchange(@Valid @RequestBody SsoExchangeRequest body) {
        var userId = redisSsoCodeStore.consumeCode(body.getCode());
        if (userId == null) {
            return ResponseFactory.error(401, "Invalid or expired code");
        }
        String token = authService.issueToken(userId);
        return ResponseFactory.ok(new SsoExchangeResponse(token, userId));
    }

    private static String readCachedRequestBody(HttpServletRequest request) {
        ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        if (wrapper != null) {
            Charset charset = request.getCharacterEncoding() == null
                    ? StandardCharsets.UTF_8
                    : Charset.forName(request.getCharacterEncoding());
            return new String(wrapper.getContentAsByteArray(), charset);
        }
        return "";
    }
}
