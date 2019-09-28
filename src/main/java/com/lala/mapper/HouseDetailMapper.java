package com.lala.mapper;

import com.lala.entity.HouseDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author lala
 */

@Mapper
@Repository
public interface HouseDetailMapper {
    void save(HouseDetail houseDetail);
    HouseDetail findOneById(@Param(value = "id") Long id);
}
