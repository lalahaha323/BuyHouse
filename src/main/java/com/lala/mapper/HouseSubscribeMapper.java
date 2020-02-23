package com.lala.mapper;

import com.lala.entity.HouseSubscribe;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lala
 */
@Mapper
@Repository
public interface HouseSubscribeMapper {
    HouseSubscribe findOneByHIdAndUId(@Param(value = "houseId")Long houseId, @Param(value = "UserId")Long userId);
    List<HouseSubscribe> findAllByHouseSubscribe(HouseSubscribe houseSubscribe);
    int countHouseSubscribe(HouseSubscribe houseSubscribe);
}
