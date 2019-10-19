package com.lala.service.impl;

import com.lala.entity.House;
import com.lala.entity.HouseTag;
import com.lala.enums.ResultEnum;
import com.lala.mapper.HouseMapper;
import com.lala.mapper.HouseTagMapper;
import com.lala.service.HouseTagService;
import com.lala.service.result.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author lala
 */

@Service
public class HouseTagServiceImpl implements HouseTagService {
    @Autowired
    HouseMapper houseMapper;
    @Autowired
    HouseTagMapper houseTagMapper;
    @Override
    public ServiceResult deleteTagsByHouseIdAndTag(Long id, String tag) {
        House house = houseMapper.findOneById(id);
        if (house == null) {
            return ServiceResult.ofResultEnum(ResultEnum.ERROR_EMPTY_HOUSE);
        }

        HouseTag houseTag = houseTagMapper.findByHouseIdAndName(id, tag);
        if (houseTag == null) {
            return ServiceResult.ofResultEnum(ResultEnum.ERROR_EMPTY_HOUSETAG);
        }

        houseTagMapper.deleteById(houseTag.getId());
        return ServiceResult.ofResultEnum(ResultEnum.SUCCESS);
    }

    @Override
    public ServiceResult addTagByHouseIdAndTag(Long houseId, String tag) {
        House house = houseMapper.findOneById(houseId);
        if (house == null) {
            return ServiceResult.ofResultEnum(ResultEnum.ERROR_EMPTY_HOUSE);
        }

        HouseTag houseTag = houseTagMapper.findByHouseIdAndName(houseId, tag);
        if (houseTag != null) {
            return ServiceResult.ofResultEnum(ResultEnum.ERROR_EMPTY_HOUSETAG);
        }

        HouseTag newHouseTag = new HouseTag(houseId, tag);
        houseTagMapper.saveOne(newHouseTag);
        return ServiceResult.ofResultEnum(ResultEnum.SUCCESS);
    }
}
