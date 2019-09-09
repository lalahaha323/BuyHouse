package com.lala.service.impl;

import com.lala.entity.SubwayStation;
import com.lala.mapper.SubwayStationMapper;
import com.lala.service.SubwayService;
import com.lala.service.SubwayStationService;
import com.lala.service.result.ServiceResult;
import com.lala.web.dto.SubwayStationDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lala
 */

@Service
public class SubwayStationServiceImpl implements SubwayStationService {

    @Autowired
    SubwayStationMapper subwayStationMapper;
    @Autowired
    ModelMapper modelMapper;

    @Override
    public ServiceResult findAllStationBySubway(Long subwayId) {
        List<SubwayStation> subwayStationList = subwayStationMapper.findAllBySubwayId(subwayId);
        if (subwayStationList.isEmpty()) {
            return ServiceResult.ofSuccess(subwayStationList);
        }
        List<SubwayStationDTO> subwayStationDTOList = new ArrayList<>();
        for(SubwayStation subwayStation : subwayStationList) {
            SubwayStationDTO subwayStationDTO = modelMapper.map(subwayStation, SubwayStationDTO.class);
            subwayStationDTOList.add(subwayStationDTO);
        }
        return ServiceResult.ofSuccess(subwayStationDTOList);
    }
}
