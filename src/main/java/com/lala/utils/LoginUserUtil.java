package com.lala.utils;

import com.lala.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.regex.Pattern;

/**
 * @author lala
 */
public class LoginUserUtil {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

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

    /** 验证邮箱 **/
    public static boolean checkEmail(String target) {
        return EMAIL_PATTERN.matcher(target).matches();
    }
}
