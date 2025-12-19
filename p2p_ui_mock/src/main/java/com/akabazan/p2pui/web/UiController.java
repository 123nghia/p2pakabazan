package com.akabazan.p2pui.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UiController {

    @GetMapping({"/", "/sso"})
    public String index() {
        return "forward:/index.html";
    }
}
