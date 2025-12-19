package com.akabazan.partner.web;

import com.akabazan.partner.web.dto.PartnerUserIdentity;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/session")
public class PartnerSessionController {

    static final String SESSION_USER_KEY = "PARTNER_USER";

    @PostMapping("/login")
    public PartnerUserIdentity login(@RequestBody PartnerUserIdentity request, HttpSession session) {
        session.setAttribute(SESSION_USER_KEY, request);
        return request;
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<PartnerUserIdentity> me(HttpSession session) {
        Object value = session.getAttribute(SESSION_USER_KEY);
        if (value instanceof PartnerUserIdentity user) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(401).build();
    }
}

