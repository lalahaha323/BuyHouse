package com.lala.mapper;

import com.lala.entity.Subway;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lala
 */

@Mapper
@Repository
public interface SubwayMapper {
    /** 根据城市名称获取所有该城市的地铁线路信息 **/
    List<Subway> findAllByCityEnName(@Param(value = "cityEnName") String cityEnName);
    Subway findBySubwayId(@Param(value = "id") Long id);
}
