package com.lala.security;

import com.lala.entity.User;
import com.lala.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 自定义认证实现
 * @author lala
 */
public class AuthProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        /** 用户输入的用户名和密码 **/
        String name = authentication.getName();
        String inputPassword = (String) authentication.getCredentials();

        User user = userService.finUserByName(name);
        if(user == null) {
            throw new AuthenticationCredentialsNotFoundException("loginError");
        }
        if(encoder.matches(inputPassword, user.getPassword()))
            return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        throw new BadCredentialsException("loginError");
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
