package com.lala.service;

import com.lala.elasticsearch.BaiduMapLocation;
import com.lala.entity.SupportAddress;
import com.lala.enums.LevelEnum;
import com.lala.service.result.ServiceResult;
import com.lala.web.dto.SupportAddressDTO;

import java.util.Map;

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
    Map<LevelEnum, SupportAddressDTO> findCityAndRegionByCNameAndRName(String cityEnName, String regionEnName);
    /** 根据城市英文名称获取该城市信息 **/
    SupportAddressDTO findCityByCityEnName(String cityEnName);
    /** 根据区域英文名称获取该区域信息 **/
    SupportAddressDTO findRegionByRegionEnName(String regionEnName);
    /** 根据城市以及具体地址名称获取经纬度 **/
    ServiceResult getBaiduMapLocationByCity(String city, String region);
    /** 上传至百度云地理服务lbs **/
    ServiceResult lbsUpload(BaiduMapLocation baiduMapLocation,
                            int area,
                            String title,
                            int price,
                            String address,
                            Long houseId,
                            String tags);
}
