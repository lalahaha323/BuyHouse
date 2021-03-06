package com.lala.mapper;

import com.lala.elasticsearch.RentSearch;
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
    House findOneById(@Param(value = "id") Long id);
    void update(House house);
    void updateStatus(@Param(value = "id") Long id, @Param(value = "status") int status);
    /** user查询所有的房屋列表 **/
    List<House> userFindAllHousesBySearch(RentSearch rentSearch);
    /** 通过List<ID>查找所有ID的房源 **/
    List<House> findAllByIdList(@Param("houseIds") List<Long> houseIds);
}
