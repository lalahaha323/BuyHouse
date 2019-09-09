package com.lala.service;

import com.lala.service.result.ServiceResult;
import com.lala.web.dto.SupportAddressDTO;

/**
 * 地址服务接口
 * @author lala
 */


public interface SupportAddressService {

    /** 获取所有支持的城市列表 **/
    ServiceResult findAllCities();
    /** 根据城市英文简写获取该城市所有支持的区域信息 **/
    ServiceResult findAllRegionsByCityName(String cityEnName);
    /** 根据城市英文简写获取具体区域信息，bj获取一级区域信息，xx县获取二级区域信息 **/
    ServiceResult findCityAndRegionByCNameAndRName(String cityEnName, String regionEnName);
}
