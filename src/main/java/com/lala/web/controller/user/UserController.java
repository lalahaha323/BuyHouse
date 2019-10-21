package com.lala.web.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 关于用户的
 * @author lala
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @GetMapping("/login")
    public String loginPage() {
        return "user/login";
    }

    @GetMapping("/center")
    public String centerPage() {
        return "user/center";
    }
}

