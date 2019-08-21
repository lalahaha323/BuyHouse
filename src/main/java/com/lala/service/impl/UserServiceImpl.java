package com.lala.service.impl;

import com.lala.entity.User;
import com.lala.mapper.UserMapper;
import com.lala.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author lala
 */

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User finUserByName(String name) {
        return userMapper.findByName(name);
    }
}
