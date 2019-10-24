package com.lala.mapper;

import com.lala.entity.HouseTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lala
 */
@Mapper
@Repository
public interface HouseTagMapper {
    void save(List<HouseTag> houseTagList);
    List<HouseTag> findAllById(@Param(value = "id") Long id);
    HouseTag findByHouseIdAndName(@Param(value = "house_id") Long houseId,
                                  @Param(value = "name") String name);
    void deleteById(@Param(value = "id")Long id);
    void saveOne(HouseTag houseTag);
    List<HouseTag> findAllByIdList(@Param(value = "houseIdList") List<Long> houseIdList);
}
