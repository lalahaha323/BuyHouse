package com.lala.web.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 后台管理控制，所有后台的请求都在这里进行处理
 * @author lala
 */

@Controller
@RequestMapping("/admin")
public class AdminController {

    /** 后台管理中心 **/
    @GetMapping("/center")
    public String adminCenterPage() {
        return "admin/center";
    }

    /** 欢迎页 **/
    @GetMapping("/welcome")
    public String welcomePage() {
        return "admin/welcome";
    }

    /** 管理员登录页 **/
    @GetMapping("/login")
    public String adminLoginPage() {
        return "admin/login";
    }

}
