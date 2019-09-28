package com.lala.service;

import com.lala.entity.Subway;
import com.lala.service.result.ServiceResult;
import com.lala.web.dto.SubwayDTO;

/**
 * @author lala
 */
public interface SubwayService {

    /** 根据城市英文简写获取该城市所有地铁线路 **/
    ServiceResult findAllSubwayByCityEnName(String cityEnName);
    /** 获取某一条地铁线 **/
    SubwayDTO findSubwayBySubwayLineId(Long id);
}
