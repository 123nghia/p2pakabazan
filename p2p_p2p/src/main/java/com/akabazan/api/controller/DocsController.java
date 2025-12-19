package com.akabazan.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DocsController {

    @GetMapping({"/api/docs", "/api/docs/"})
    public String docs() {
        return "redirect:/api/swagger-ui/index.html";
    }
}

