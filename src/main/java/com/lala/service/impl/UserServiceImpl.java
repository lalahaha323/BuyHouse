package com.lala.service.impl;

import com.lala.entity.Role;
import com.lala.entity.User;
import com.lala.mapper.RoleMapper;
import com.lala.mapper.UserMapper;
import com.lala.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lala
 */

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public User finUserByName(String name) {
        User user =  userMapper.findByName(name);
        if(user == null) {
            return null;
        }

        List<Role> roles = roleMapper.findRolesByUserId(user.getId());
        if(roles == null || roles.isEmpty()) {
            throw new DisabledException("权限非法");
        }

        List<GrantedAuthority> authorityList = new ArrayList<>();
        roles.forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_" + role.getName())));

        user.setAuthorityList(authorityList);
        return user;
    }
}