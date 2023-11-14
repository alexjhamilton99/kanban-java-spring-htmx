package com.alexjhamilton.kanban.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class AuthController {

    @GetMapping("/login")
    String getLoginPage() {
        return "login";
    }

}
