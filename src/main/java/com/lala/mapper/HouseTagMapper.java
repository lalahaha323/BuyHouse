package com.lala.mapper;

import com.lala.entity.HouseTag;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lala
 */
@Mapper
@Repository
public interface HouseTagMapper {
    void save(List<HouseTag> houseTagList);
}
