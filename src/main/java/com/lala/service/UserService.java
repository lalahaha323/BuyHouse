package com.lala.service;

import com.lala.entity.User;
import com.lala.service.result.ServiceResult;

/**
 * @author lala
 */
public interface UserService {
    User finUserByName(String name);
    ServiceResult updateUser(String profile, String value);
    ServiceResult getUserById(Long id);
}
