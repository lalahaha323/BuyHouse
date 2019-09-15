package com.lala.service.impl;

import com.lala.entity.House;
import com.lala.entity.HouseDetail;
import com.lala.entity.Subway;
import com.lala.entity.SubwayStation;
import com.lala.enums.ResultEnum;
import com.lala.mapper.HouseDetailMapper;
import com.lala.mapper.HouseMapper;
import com.lala.mapper.SubwayMapper;
import com.lala.mapper.SubwayStationMapper;
import com.lala.service.HouseService;
import com.lala.service.result.ServiceResult;
import com.lala.utils.LoginUserUtil;
import com.lala.web.form.HouseForm;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author lala
 */

@Service
public class HouseServiceImpl implements HouseService {
    @Autowired
    SubwayMapper subwayMapper;
    @Autowired
    SubwayStationMapper subwayStationMapper;
    @Autowired
    HouseMapper houseMapper;
    @Autowired
    HouseDetailMapper houseDetailMapper;
    @Autowired
    ModelMapper modelMapper;
    @Override
    public void save(HouseForm houseForm) {
        HouseDetail houseDetail = FillinDetailInfo(houseForm);

        House house = new House();
        modelMapper.map(houseForm, house);
        Date now = new Date();
        house.setCreateTime(now);
        house.setLastUpdateTime(now);
        house.setAdminId(LoginUserUtil.getLoginUserId());
        house.setStatus(0);
        houseMapper.save(house);

        houseDetail.setHouseId(house.getId());
        houseDetailMapper.save(houseDetail);





    }

    /** 房源详细信息对象填充 **/
    private HouseDetail FillinDetailInfo(HouseForm houseForm) {
        HouseDetail houseDetail = new HouseDetail();
        Subway subway = subwayMapper.findBySubwayId(houseForm.getSubwayLineId());
        SubwayStation subwayStation = subwayStationMapper.findBySubwayStationId(houseForm.getSubwayStationId());
        houseDetail.setDescription(houseForm.getDescription());
        houseDetail.setLayoutDesc(houseForm.getLayoutDesc());
        houseDetail.setTraffic(houseForm.getTraffic());
        houseDetail.setRoundService(houseForm.getRoundService());
        houseDetail.setRentWay(houseForm.getRentWay());
        houseDetail.setAddress(houseForm.getDetailAddress());
        houseDetail.setSubwayLineId(houseForm.getSubwayLineId());
        houseDetail.setSubwayLineName(subway.getName());
        houseDetail.setSubwayStationId(houseForm.getSubwayStationId());
        houseDetail.setSubwayStationName(subwayStation.getName());
        return houseDetail;
    }
}
