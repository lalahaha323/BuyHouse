package com.lala.web.controller.user;

import com.lala.enums.ResultEnum;
import com.lala.service.UserService;
import com.lala.service.result.ServiceResult;
import com.lala.utils.LoginUserUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lala
 */
@RestController
@RequestMapping("/api/user")
public class AboutUserController {

    @Autowired
    UserService userService;

    /** 修改个人信息 **/
    @PostMapping(value = "/info")
    public ServiceResult updateUserInfo(@RequestParam(value = "profile") String profile,
                                        @RequestParam(value = "value") String value) {
        if (StringUtils.isEmpty(profile)) {
            return ServiceResult.ofResultEnum(ResultEnum.ERROR_EMPTY_DATA);
        }

        if ("email".equals(profile) && !LoginUserUtil.checkEmail(value)) {
            return ServiceResult.ofResultEnum(ResultEnum.ERROR_MISMATCH_EMAIL);
        }

        return userService.updateUser(profile, value);
    }

}
