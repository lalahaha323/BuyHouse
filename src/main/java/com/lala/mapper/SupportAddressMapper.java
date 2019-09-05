package com.lala.mapper;

import com.lala.entity.SupportAddress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lala
 */

@Repository
@Mapper
public interface SupportAddressMapper {

    /** 获取所有对应行政级别的名称信息 **/
    List<SupportAddress> findAllByLevel(String level);
    /** 根据城市名称获取所有该城市的区域信息 **/
    List<SupportAddress> findAllByLevelAndBelongTo(@Param(value = "level") String level,
                                                   @Param(value = "belongTo") String belongTo);
}
