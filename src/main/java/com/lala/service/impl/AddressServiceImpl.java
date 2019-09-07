package com.lala.service.impl;

import com.lala.entity.SupportAddress;
import com.lala.enums.LevelEnum;
import com.lala.enums.ResultEnum;
import com.lala.mapper.SupportAddressMapper;
import com.lala.service.AddressService;
import com.lala.service.result.ServiceResult;
import com.lala.web.dto.SupportAddressDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lala
 */

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    SupportAddressMapper supportAddressMapper;

    @Autowired
    ModelMapper modelMapper;

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
}
