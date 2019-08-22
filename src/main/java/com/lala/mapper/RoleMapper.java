package com.lala.mapper;

import com.lala.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lala
 */
@Mapper
@Repository
public interface RoleMapper {

    List<Role> findRolesByUserId(@Param("userId") Long userId);
}
