package com.lala.service.impl;

import com.lala.entity.*;
import com.lala.enums.ResultEnum;
import com.lala.mapper.HouseDetailMapper;
import com.lala.mapper.HouseMapper;
import com.lala.mapper.SubwayMapper;
import com.lala.mapper.SubwayStationMapper;
import com.lala.service.HouseService;
import com.lala.service.result.ServiceResult;
import com.lala.utils.LoginUserUtil;
import com.lala.web.form.HouseForm;
import com.lala.web.form.PhotoForm;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    /** 图片信息填充 **/
    private List<HousePicture> FillinPictureInfo(HouseForm houseForm, Long houseId) {
        if(houseForm.getPhotos() == null || houseForm.getPhotos().isEmpty()) {
            return null;
        }
        List<HousePicture> pictureList = new ArrayList<>();
        for(PhotoForm photoForm : houseForm.getPhotos()) {
            HousePicture housePicture = new HousePicture();
            housePicture.setHouseId(houseId);
            housePicture.setCdnPrefix("xx");
            housePicture.setPath(photoForm.getPath());
            housePicture.setWidth(photoForm.getWidth());
            housePicture.setHeight(photoForm.getHeight());
            pictureList.add(housePicture);
        }
        return pictureList;
    }
}
