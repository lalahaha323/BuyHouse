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
    /** 根据一级名称和级别city查找对应的一级信息 **/
    SupportAddress findByCityEnNameAndLevel(@Param(value = "enName") String enName,
                                            @Param(value = "level") String level);
    /** 根据二级名称和上一级名称查找对应的二级区域信息 **/
    SupportAddress findByRegionEnNameAndLevel(@Param(value = "enName") String enName,
                                              @Param(value = "belongTo") String belongTo);
}
