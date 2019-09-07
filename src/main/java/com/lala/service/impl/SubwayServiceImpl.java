package com.lala.service.impl;

import com.lala.entity.Subway;
import com.lala.mapper.SubwayMapper;
import com.lala.service.SubwayService;
import com.lala.service.result.ServiceResult;
import com.lala.web.dto.SubwayDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lala
 */

@Service
public class SubwayServiceImpl implements SubwayService {

    @Autowired
    SubwayMapper subwayMapper;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public ServiceResult findAllSubwayByCityEnName(String cityEnName) {
        List<Subway> subwayList = subwayMapper.findAllByCityEnName(cityEnName);
        if(subwayList.isEmpty()) {
            return ServiceResult.ofSuccess(subwayList);
        }
        List<SubwayDTO> subwayDTOList = new ArrayList<>();
        for(Subway subway : subwayList) {
            SubwayDTO subwayDTO = modelMapper.map(subway, SubwayDTO.class);
            subwayDTOList.add(subwayDTO);
        }
        return ServiceResult.ofSuccess(subwayDTOList);
    }
}
