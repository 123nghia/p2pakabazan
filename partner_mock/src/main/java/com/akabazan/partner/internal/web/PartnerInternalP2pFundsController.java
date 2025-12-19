package com.akabazan.partner.internal.web;

import com.akabazan.partner.internal.funds.BalancesResponse;
import com.akabazan.partner.internal.funds.LockFundsRequest;
import com.akabazan.partner.internal.funds.LockFundsResponse;
import com.akabazan.partner.internal.funds.PartnerFundsService;
import com.akabazan.partner.internal.funds.TransferFundsRequest;
import com.akabazan.partner.internal.funds.TransferFundsResponse;
import com.akabazan.partner.internal.funds.UnlockFundsRequest;
import com.akabazan.partner.internal.funds.UnlockFundsResponse;
import com.akabazan.partner.internal.security.P2pAuthResult;
import com.akabazan.partner.internal.security.P2pHmacAuthenticator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.WebUtils;

@RestController
@RequestMapping("/internal/p2p")
public class PartnerInternalP2pFundsController {

    private final P2pHmacAuthenticator authenticator;
    private final PartnerFundsService fundsService;

    public PartnerInternalP2pFundsController(P2pHmacAuthenticator authenticator,
                                             PartnerFundsService fundsService) {
        this.authenticator = authenticator;
        this.fundsService = fundsService;
    }

    @GetMapping("/users/{externalUserId}/balances")
    public ResponseEntity<?> balances(HttpServletRequest request,
                                      @PathVariable String externalUserId,
                                      @RequestParam(required = false) String asset) {
        P2pAuthResult auth = authenticator.authenticate(request, "");
        if (!auth.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("message", auth.getErrorMessage()));
        }
        try {
            BalancesResponse response = fundsService.getBalances(externalUserId, asset);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/funds/lock")
    public ResponseEntity<?> lock(HttpServletRequest request, @Valid @RequestBody LockFundsRequest body) {
        String rawBody = readCachedRequestBody(request);
        P2pAuthResult auth = authenticator.authenticate(request, rawBody);
        if (!auth.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("message", auth.getErrorMessage()));
        }
        try {
            LockFundsResponse response = fundsService.lock(body);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/funds/unlock")
    public ResponseEntity<?> unlock(HttpServletRequest request, @Valid @RequestBody UnlockFundsRequest body) {
        String rawBody = readCachedRequestBody(request);
        P2pAuthResult auth = authenticator.authenticate(request, rawBody);
        if (!auth.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("message", auth.getErrorMessage()));
        }
        try {
            UnlockFundsResponse response = fundsService.unlock(body);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/funds/transfer")
    public ResponseEntity<?> transfer(HttpServletRequest request, @Valid @RequestBody TransferFundsRequest body) {
        String rawBody = readCachedRequestBody(request);
        P2pAuthResult auth = authenticator.authenticate(request, rawBody);
        if (!auth.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("message", auth.getErrorMessage()));
        }
        try {
            TransferFundsResponse response = fundsService.transfer(body);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
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

