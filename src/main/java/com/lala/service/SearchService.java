package com.lala.service;

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
}
