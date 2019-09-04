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
    public ServiceResult<SupportAddressDTO> findAllCities() {
        List<SupportAddress> supportAddressList = supportAddressMapper.findAllByLevel(LevelEnum.CITY.getValue());
        if(supportAddressList.isEmpty()) {
            return ServiceResult.ofResultEnum(ResultEnum.NOT_FOUND);
        }

        List<SupportAddressDTO> supportAddressDTOList = new ArrayList<>();
        for (SupportAddress supportAddress : supportAddressList) {
            SupportAddressDTO supportAddressDTO = modelMapper.map(supportAddress, SupportAddressDTO.class);
            supportAddressDTOList.add(supportAddressDTO);
        }
        return ServiceResult.ofSuccess(supportAddressDTOList);
    }
}
