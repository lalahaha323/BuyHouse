package com.lala.service;

import com.lala.entity.SubwayStation;
import com.lala.service.result.ServiceResult;

import java.util.List;

/**
 * @author lala
 */
public interface SubwayStationService {

    /** 获取地铁线路所有的站点 **/
    ServiceResult findAllStationBySubway(Long subwayId);
}
