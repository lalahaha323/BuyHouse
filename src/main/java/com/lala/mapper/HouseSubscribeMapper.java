package com.lala.mapper;

import com.lala.entity.HouseSubscribe;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author lala
 */
@Mapper
@Repository
public interface HouseSubscribeMapper {
    HouseSubscribe findOneByHIdAndUId(@Param(value = "houseId")Long houseId, @Param(value = "UserId")Long userId);
}
