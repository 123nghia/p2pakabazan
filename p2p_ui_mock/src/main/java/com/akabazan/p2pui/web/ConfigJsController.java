package com.akabazan.p2pui.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigJsController {

    @Value("${p2p.api-base-url:http://localhost:9999}")
    private String apiBaseUrl;

    @GetMapping(value = "/config.js", produces = "application/javascript")
    public ResponseEntity<String> configJs() {
        String safeUrl = (apiBaseUrl == null ? "" : apiBaseUrl).replace("\\", "\\\\").replace("\"", "\\\"");
        String js = "window.P2P_UI_CONFIG = { apiBaseUrl: \"" + safeUrl + "\" };";
        return ResponseEntity.ok().contentType(MediaType.valueOf("application/javascript")).body(js);
    }
}
