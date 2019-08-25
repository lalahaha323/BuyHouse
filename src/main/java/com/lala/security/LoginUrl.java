package com.lala.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 基于角色的登录入口控制器
 * 之前想进入/admin/center就进入/admin/center，想进入/user/center就进入/user/center，但是这样不可以
 * 想一下，登录之后才可以进入对应的页面
 * @author lala
 */
public class LoginUrl extends LoginUrlAuthenticationEntryPoint {

    private final Map<String, String> entryMap;
    private PathMatcher pathMatcher = new AntPathMatcher();

    public LoginUrl(String loginFormUrl) {
        super(loginFormUrl);
        entryMap = new HashMap<>();

        /** 普通用户登录入口映射，所有user/**请求都要先经过/user/login **/
        entryMap.put("/user/**", "/user/login");
        /** 管理员登录入口映射，所有admin/**请求都要先经过/admin/login **/
        entryMap.put("/admin/**", "/admin/login");
    }

    /** 根据请求跳转到指定的页面，父类是默认使用loginFormUrl **/
    @Override
    protected String determineUrlToUseForThisRequest(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        String uri = request.getRequestURI().replace(request.getContextPath(), "");

        for (Map.Entry<String, String> authEntry : this.entryMap.entrySet()) {
            if(this.pathMatcher.match(authEntry.getKey(), uri)){
                return authEntry.getValue();
            }
        }
        return super.determineUrlToUseForThisRequest(request, response, exception);
    }
}
