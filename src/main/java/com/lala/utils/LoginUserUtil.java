package com.lala.utils;

import com.lala.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author lala
 */
public class LoginUserUtil {
    public static User load(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal != null && principal instanceof User) {
            return (User) principal;
        }
        return null;
    }

    public static Long getLoginUserId() {
        User user = load();
        if(user == null) {
            return -1L;
        }
        return user.getId();
    }
}
