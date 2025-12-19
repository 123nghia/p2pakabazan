package com.akabazan.partner.web;

import com.akabazan.partner.config.P2pProperties;
import com.akabazan.partner.sso.P2pSsoClient;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PartnerRedirectController {

    private final P2pSsoClient p2pSsoClient;
    private final P2pProperties p2pProperties;

    public PartnerRedirectController(P2pSsoClient p2pSsoClient, P2pProperties p2pProperties) {
        this.p2pSsoClient = p2pSsoClient;
        this.p2pProperties = p2pProperties;
    }

    @GetMapping(value = "/go/p2p", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<?> goToP2p(@RequestParam String externalUserId,
                                     @RequestParam(required = false) String email,
                                     @RequestParam(required = false) String username,
                                     @RequestParam(defaultValue = "VERIFIED") String kycStatus) {
        try {
            String code = p2pSsoClient.issueOneTimeCode(new P2pSsoClient.SsoIssueRequest(
                    externalUserId,
                    email,
                    username,
                    kycStatus));

            String redirectUrl = trimTrailingSlash(p2pProperties.getUiUrl())
                    + "/sso?code=" + URLEncoder.encode(code, StandardCharsets.UTF_8);

            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(redirectUrl))
                    .build();
        } catch (Exception e) {
            String html = """
                    <html><body style="font-family:system-ui,Segoe UI,Arial,sans-serif">
                      <h2>Partner Mock: redirect failed</h2>
                      <pre style="white-space:pre-wrap;background:#f6f7fb;padding:12px;border-radius:8px">%s</pre>
                      <p><a href="/">Back</a></p>
                    </body></html>
                    """.formatted(escapeHtml(e.getMessage() == null ? e.toString() : e.getMessage()));

            return ResponseEntity.status(500).contentType(MediaType.TEXT_HTML).body(html);
        }
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

    private static String escapeHtml(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
