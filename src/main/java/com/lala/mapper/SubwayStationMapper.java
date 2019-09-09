package com.lala.mapper;

import com.lala.entity.SubwayStation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lala
 */

@Mapper
@Repository
public interface SubwayStationMapper {

    /** 根据地铁线路ＩＤ获取所有该线路的站点信息 **/
    List<SubwayStation> findAllBySubwayId(@Param(value = "subwayId") Long subwayId);
}
