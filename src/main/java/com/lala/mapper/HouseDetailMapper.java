package com.lala.mapper;

import com.lala.entity.HouseDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lala
 */

@Mapper
@Repository
public interface HouseDetailMapper {
    void save(HouseDetail houseDetail);
    HouseDetail findOneById(@Param(value = "id") Long id);
    void update(HouseDetail houseDetail);
    List<HouseDetail> findAllById(@Param(value = "houseIdList") List<Long> houseIdList);
}
