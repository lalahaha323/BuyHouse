package com.lala.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.lala.elasticsearch.BaiduAPI;
import com.lala.elasticsearch.BaiduMapLocation;
import com.lala.entity.SupportAddress;
import com.lala.enums.LevelEnum;
import com.lala.enums.ResultEnum;
import com.lala.mapper.SupportAddressMapper;
import com.lala.service.SupportAddressService;
import com.lala.service.result.ServiceResult;
import com.lala.web.dto.SupportAddressDTO;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lala
 */

@Service
public class SupportAddressServiceImpl implements SupportAddressService {

    @Autowired
    SupportAddressMapper supportAddressMapper;

    @Autowired
    ModelMapper modelMapper;

    @Value("${baidu_map_key}")
    private String BAIDU_MAP_KEY;

    @Override
    public ServiceResult findAllCities() {
        List<SupportAddress> cityList = supportAddressMapper.findAllByLevel(LevelEnum.CITY.getValue());
        if(cityList.isEmpty()) {
            return ServiceResult.ofResultEnum(ResultEnum.NOT_FOUND);
        }

        List<SupportAddressDTO> cityDTOList = new ArrayList<>();
        for (SupportAddress supportAddress : cityList) {
            SupportAddressDTO supportAddressDTO = modelMapper.map(supportAddress, SupportAddressDTO.class);
            cityDTOList.add(supportAddressDTO);
        }
        return ServiceResult.ofSuccess(cityDTOList);
    }

    @Override
    public ServiceResult findAllRegionsByCityName(String cityEnName) {
        if(cityEnName == null) {
            return ServiceResult.ofResultEnum(ResultEnum.NOT_VALID_PARAM);
        }
        /** 获取到例如ｂｊ所有的ｒｅｇｉｏｎ的信息 **/
        List<SupportAddress> regionList = supportAddressMapper.findAllByLevelAndBelongTo(LevelEnum.REGION.getValue(), cityEnName);
        if(regionList.isEmpty()) {
            return ServiceResult.ofResultEnum(ResultEnum.NOT_FOUND);
        }
        List<SupportAddressDTO> regionDTOList = new ArrayList<>();
        for (SupportAddress supportAddress : regionList) {
            SupportAddressDTO supportAddressDTO = modelMapper.map(supportAddress, SupportAddressDTO.class);
            regionDTOList.add(supportAddressDTO);
        }
        return ServiceResult.ofSuccess(regionDTOList);
    }

    @Override
    public Map<LevelEnum, SupportAddressDTO> findCityAndRegionByCNameAndRName(String cityEnName, String regionEnName) {
        Map<LevelEnum, SupportAddressDTO> result = new HashMap<>();

        SupportAddress city = supportAddressMapper.findByCityEnNameAndLevel(cityEnName, LevelEnum.CITY.getValue());
        if(city != null) {
            result.put(LevelEnum.CITY, modelMapper.map(city, SupportAddressDTO.class));
        }
        SupportAddress region = supportAddressMapper.findByRegionEnNameAndBelongTo(regionEnName, cityEnName);
        if(region != null) {
            result.put(LevelEnum.REGION, modelMapper.map(region, SupportAddressDTO.class));
        }

        return result;
    }

    @Override
    public SupportAddressDTO findCityByCityEnName(String cityEnName) {
        SupportAddress supportAddress = supportAddressMapper.findByCityEnNameAndLevel(cityEnName, LevelEnum.CITY.getValue());
        SupportAddressDTO supportAddressDTO = modelMapper.map(supportAddress, SupportAddressDTO.class);
        return supportAddressDTO;
    }

    @Override
    public SupportAddressDTO findRegionByRegionEnName(String regionEnName) {
        SupportAddress supportAddress = supportAddressMapper.findByCityEnNameAndLevel(regionEnName, LevelEnum.REGION.getValue());
        SupportAddressDTO supportAddressDTO = modelMapper.map(supportAddress, SupportAddressDTO.class);
        return supportAddressDTO;
    }

    /** 根据城市以及具体地址查询经纬度 **/
    @Override
    public ServiceResult getBaiduMapLocationByCity(String city, String region) {

        String addressEncode = null;
        String cityEncode = null;

        try {
            addressEncode = URLEncoder.encode(region, "UTF-8");
            cityEncode = URLEncoder.encode(city, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return ServiceResult.ofResultEnum(ResultEnum.ERROR_ENCODE);
        }

        HttpClient httpClient = HttpClients.createDefault();
        StringBuilder stringBuilder = new StringBuilder(BaiduAPI.BAIDU_MAP_GEOCONV_API);
        stringBuilder.append("address=").append(addressEncode).append("&")
                     .append("city=").append(cityEncode).append("&")
                     .append("output=json&")
                     .append("ak=").append(BAIDU_MAP_KEY);

        HttpGet get = new HttpGet(stringBuilder.toString());
        try {
            HttpResponse response = httpClient.execute(get);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                return ServiceResult.ofResultEnum(ResultEnum.BAD_REQUEST);
            }

            String result = EntityUtils.toString(response.getEntity(), "UTF-8");
            JSONObject baiDuResponse = (JSONObject) JSONObject.parse(result);
            int status = Integer.parseInt(baiDuResponse.get("status").toString());
            if (status != 0) {
                return ServiceResult.ofResultEnum(ResultEnum.ERROR_ENCODE);
            }

            BaiduMapLocation location = new BaiduMapLocation();
            JSONObject resultObject = (JSONObject) JSONObject.parse(baiDuResponse.get("result").toString());
            JSONObject locationObject = (JSONObject) resultObject.get("location");
            location.setLon(Double.parseDouble(locationObject.get("lng").toString()));
            location.setLat(Double.parseDouble(locationObject.get("lat").toString()));
            return ServiceResult.ofSuccess(location);

        } catch (IOException e) {
            e.printStackTrace();
            return ServiceResult.ofResultEnum(ResultEnum.BAD_REQUEST);
        }
        return null;
    }
}
