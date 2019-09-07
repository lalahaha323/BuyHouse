package com.lala.service;

import com.lala.service.result.ServiceResult;

/**
 * @author lala
 */
public interface SubwayService {

    /** 根据城市英文简写获取该城市所有地铁线路 **/
    ServiceResult findAllSubwayByCityEnName(String cityEnName);
}
