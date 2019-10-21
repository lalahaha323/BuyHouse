package com.lala.mapper;

import com.lala.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author lala
 */

@Mapper
@Repository
public interface UserMapper {

    User findByName(@Param("name") String name);
    User findById(@Param("id") Long id);
    void updateUser(User user);
}
