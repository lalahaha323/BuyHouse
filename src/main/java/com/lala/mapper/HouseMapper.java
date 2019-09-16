package com.lala.mapper;

import com.lala.entity.House;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author lala
 */

@Mapper
@Repository
public interface HouseMapper {
    void save(House house);
}
