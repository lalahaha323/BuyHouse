package com.lala.web.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 后台管理控制，所有后台的请求都在这里进行处理
 * @author lala
 */

@Controller
public class AdminController {

    /** 后台管理中心 **/
    @GetMapping("/admin/center")
    public String adminCenterPage() {
        return "admin/center";
    }

    /** 欢迎页 **/
    @GetMapping("/admin/welcome")
    public String welcomePage() {
        return "admin/welcome";
    }

    /** 管理员登录页 **/
    @GetMapping("/admin/login")
    public String adminLoginPage() {
        return "admin/login";
    }

}
