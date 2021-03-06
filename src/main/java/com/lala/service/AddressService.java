package com.lala.service;

import com.lala.service.result.ServiceResult;
import com.lala.web.dto.SupportAddressDTO;

/**
 * @author lala
 */
public interface AddressService {

    /** 获取所有支持的城市列表 **/
    ServiceResult<SupportAddressDTO> findAllCities();
    /** 根据城市英文简写获取该城市所有支持的区域信息 **/
    ServiceResult findAllRegionsByCityName(String cityName);
    SupportAddressDTO getCityByCityEnNameAndLevel(String cityEnName);
    SupportAddressDTO getRegionByEnNameAndBelongTo(String enName, String belongTo);
}
