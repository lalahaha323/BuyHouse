package com.lala.mapper;

import com.lala.entity.House;
import com.lala.utils.DatatableSearch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lala
 */

@Mapper
@Repository
public interface HouseMapper {
    void save(House house);
    List<House> finAll(DatatableSearch searchBody);
    int countAll();
}
