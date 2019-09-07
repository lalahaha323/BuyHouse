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
    List<Subway> findAllByCityEnName(@Param(value = "cityEnName") String cityEnName);
}
