package com.akabazan.partner.web;

import com.akabazan.partner.config.P2pProperties;
import com.akabazan.partner.sso.P2pSsoClient;
import com.akabazan.partner.web.dto.PartnerUserIdentity;
import jakarta.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/p2p")
public class PartnerP2pController {

    private final P2pSsoClient p2pSsoClient;
    private final P2pProperties p2pProperties;

    public PartnerP2pController(P2pSsoClient p2pSsoClient, P2pProperties p2pProperties) {
        this.p2pSsoClient = p2pSsoClient;
        this.p2pProperties = p2pProperties;
    }

    @PostMapping("/start")
    public ResponseEntity<StartP2pResponse> start(@RequestBody StartP2pRequest request, HttpSession session) {
        PartnerUserIdentity identity = resolveIdentity(request, session);
        if (identity == null || identity.externalUserId() == null || identity.externalUserId().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        session.setAttribute(PartnerSessionController.SESSION_USER_KEY, identity);

        String kycStatus = (request.kycStatus == null || request.kycStatus.isBlank())
                ? "VERIFIED"
                : request.kycStatus.trim();

        String code = p2pSsoClient.issueOneTimeCode(new P2pSsoClient.SsoIssueRequest(
                identity.externalUserId().trim(),
                identity.email(),
                identity.username(),
                kycStatus));

        String redirectUrl = trimTrailingSlash(p2pProperties.getUiUrl())
                + "/sso?code=" + URLEncoder.encode(code, StandardCharsets.UTF_8);

        return ResponseEntity.ok(new StartP2pResponse(code, redirectUrl));
    }

    private static PartnerUserIdentity resolveIdentity(StartP2pRequest request, HttpSession session) {
        if (request != null && request.externalUserId != null && !request.externalUserId.isBlank()) {
            return new PartnerUserIdentity(
                    request.externalUserId.trim(),
                    request.email == null ? null : request.email.trim(),
                    request.username == null ? null : request.username.trim());
        }
        Object value = session.getAttribute(PartnerSessionController.SESSION_USER_KEY);
        if (value instanceof PartnerUserIdentity user) {
            return user;
        }
        return null;
    }

    private static String trimTrailingSlash(String url) {
        if (url == null) {
            return "";
        }
        if (url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }

    public static class StartP2pRequest {
        public String externalUserId;
        public String email;
        public String username;
        public String kycStatus;
    }

    public record StartP2pResponse(String code, String redirectUrl) {}
}

