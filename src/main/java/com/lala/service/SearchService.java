package com.lala.service;

import com.lala.elasticsearch.MapSearch;
import com.lala.elasticsearch.RentSearch;
import com.lala.service.result.ServiceResult;

import java.util.List;

/**
 * @author lala
 */
public interface SearchService {
    //索引目标房源
    void index(long houseId);
    //移除房源索引
    void remove(long houseId);
    //es查询
    List<Long> esQuery(RentSearch rentSearch);
    //关键字模糊匹配
    ServiceResult fix(String prefix);
    //聚合统计小区的房源
    ServiceResult aggregateDistrictHouse(String cityEnName, String regionName, String district);
    //聚合统计城市房源信息数量
    ServiceResult aggregateHouseCountByCityEnName(String cityEnName);
    //es地图查询，返回房屋ID
    List<Long> mapQuery(String cityEnName, String orderBy, String orderDirection, int start, int size);
    //小地图查询
    List<Long> mapBoundQuery(MapSearch mapSearch);
}
