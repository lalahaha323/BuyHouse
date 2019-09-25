package com.lala.service.impl;

import com.github.pagehelper.PageHelper;
import com.lala.entity.*;
import com.lala.entity.HouseTag;
import com.lala.enums.ResultEnum;
import com.lala.mapper.*;
import com.lala.service.HouseService;
import com.lala.service.result.ResultDataTableResponse;
import com.lala.service.result.ServiceResult;
import com.lala.utils.DatatableSearch;
import com.lala.utils.LoginUserUtil;
import com.lala.web.dto.HouseDTO;
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
    HousePictureMapper housePictureMapper;
    @Autowired
    HouseTagMapper houseTagMapper;
    @Autowired
    ModelMapper modelMapper;
    @Override
    public ServiceResult save(HouseForm houseForm) {
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

        List<HousePicture> housePictureList = FillinPictureInfo(houseForm, house.getId());
        housePictureMapper.save(housePictureList);

        List<HouseTag> houseTagList = FillinTagInfo(houseForm, house.getId());
        houseTagMapper.save(houseTagList);

        return ServiceResult.ofSuccess(houseForm);
    }

    @Override
    public ResultDataTableResponse findAll(DatatableSearch searchBody) {
        List<HouseDTO> houseDTOList = new ArrayList<>();

        int start = searchBody.getStart();
        int length = searchBody.getLength();
        int pageNum = start / length + 1;
        PageHelper.startPage(pageNum + 1, length);
        List<House> houseList = houseMapper.finAll(searchBody);
        for(House house : houseList) {
            HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
            houseDTOList.add(houseDTO);
        }

        int total = houseMapper.countAll();

        ResultDataTableResponse response = new ResultDataTableResponse(ResultEnum.SUCCESS);
        response.setData(houseDTOList);
        response.setRecordsFiltered(total);
        response.setRecordsTotal(total);
        response.setDraw(searchBody.getDraw());

        return response;
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

    /** 房源Tag信息填充 **/
    private List<HouseTag> FillinTagInfo(HouseForm houseForm, Long houseId) {
        List<String> tagList = houseForm.getTags();
        if(tagList != null && !tagList.isEmpty()) {
            List<HouseTag> houseTagList = new ArrayList<>();
            for (String tag : tagList) {
                houseTagList.add(new HouseTag(houseId, tag));
            }
            return houseTagList;
        }
        return null;
    }
}
