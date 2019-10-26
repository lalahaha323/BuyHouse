package com.lala.service;

/**
 * @author lala
 */
public interface SearchService {
    //索引目标房源
    void index(long houseId);
    //移除房源索引
    void remove(long houseId);
}
