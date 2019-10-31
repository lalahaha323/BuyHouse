package com.lala.service.impl;

import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import com.lala.elasticsearch.RentSearch;
import com.lala.entity.*;
import com.lala.entity.HouseTag;
import com.lala.enums.HouseStatusEnum;
import com.lala.enums.ResultEnum;
import com.lala.mapper.*;
import com.lala.service.HouseService;
import com.lala.service.SearchService;
import com.lala.service.result.ResultDataTableResponse;
import com.lala.service.result.ServiceResult;
import com.lala.utils.DatatableSearch;
import com.lala.utils.LoginUserUtil;
import com.lala.web.Form.HouseForm;
import com.lala.web.Form.PhotoForm;
import com.lala.web.dto.HouseDTO;
import com.lala.web.dto.HouseDetailDTO;
import com.lala.web.dto.HousePictureDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
    HouseSubscribeMapper houseSubscribeMapper;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    SearchService searchService;
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
        int pageNum = start / length;
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

    @Override
    public ServiceResult findAllOne(Long id) {
        /** House **/
        House house = houseMapper.findOneById(id);
        if (house == null) {
            return ServiceResult.ofResultEnum(ResultEnum.ERROR_EMPTY_HOUSE);
        }
        HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);

        /** HouseDetail **/
        HouseDetail houseDetail = houseDetailMapper.findOneById(id);
        HouseDetailDTO houseDetailDTO = modelMapper.map(houseDetail, HouseDetailDTO.class);

        /** HouseTag **/
        List<HouseTag> houseTagList = houseTagMapper.findAllById(id);
        List<String> tags = new ArrayList<>();
        for (HouseTag houseTag : houseTagList) {
            tags.add(houseTag.getName());
        }

        /** HousePicture **/
        List<HousePicture> pictureList = housePictureMapper.findAllById(id);
        List<HousePictureDTO> pictureDTOList = new ArrayList<>();
        for (HousePicture housePicture : pictureList) {
            HousePictureDTO housePictureDTO = modelMapper.map(housePicture, HousePictureDTO.class);
            pictureDTOList.add(housePictureDTO);
        }

        /** 填充HouseDTO，通过houseDetail，PictureDTOList，Tags **/
        houseDTO.setHouseDetail(houseDetailDTO);
        houseDTO.setPictures(pictureDTOList);
        houseDTO.setTags(tags);

        /** 添加adminID **/
        if(LoginUserUtil.getLoginUserId() > 0) {
            //有登录用户
            HouseSubscribe subscribe = houseSubscribeMapper.findOneByHIdAndUId(house.getId(), LoginUserUtil.getLoginUserId());
            if(subscribe == null) {
                houseDTO.setSubscribeStatus(0);
            } else {
                houseDTO.setSubscribeStatus(subscribe.getStatus());
            }
        }

        return ServiceResult.ofSuccess(houseDTO);
    }

    /** 修改房屋 **/
    @Override
    public ServiceResult update(HouseForm houseForm) {
        House house = houseMapper.findOneById(houseForm.getId());
        if(house == null) {
            return ServiceResult.ofResultEnum(ResultEnum.ERROR_EMPTY_HOUSE);
        }

        HouseDetail houseDetail = houseDetailMapper.findOneById(house.getId());
        if(houseDetail == null) {
            return ServiceResult.ofResultEnum(ResultEnum.ERROR_EMPTY_HOUSEDETAIL);
        }

        /** 修改房屋详情 **/
        HouseDetail newhouseDetail = FillinDetailInfo(houseForm);
        newhouseDetail.setId(houseDetail.getId());
        houseDetailMapper.update(newhouseDetail);

        /** 房屋图片添加 **/
        if (houseForm.getPhotos() != null) {
            List<HousePicture> pictureList = FillinPictureInfo(houseForm, houseForm.getId());
            housePictureMapper.save(pictureList);
        }
        if(houseForm.getCover() == null) {
            houseForm.setCover(house.getCover());
        }

        /** 修改房屋基本 **/
        modelMapper.map(houseForm, house);
        house.setLastUpdateTime(new Date());
        houseMapper.update(house);
        return ServiceResult.ofResultEnum(ResultEnum.SUCCESS);
    }

    /** 修改房屋状态 **/
    @Override
    public ServiceResult updateStatus(Long id, int status) {
        House house = houseMapper.findOneById(id);
        if (house == null) {
            return ServiceResult.ofResultEnum(ResultEnum.ERROR_EMPTY_HOUSE);
        }
        if (house.getStatus() == status) {
            return ServiceResult.ofResultEnum(ResultEnum.ERROR_STATUS_NOCHANGE);
        }
        switch (house.getStatus()) {
            case 2:
                return ServiceResult.ofResultEnum(ResultEnum.ERROR_STATUS_NORENT);
            case 3:
                return ServiceResult.ofResultEnum(ResultEnum.ERROR_STATUS_NODELETE);
        }
        houseMapper.updateStatus(id, status);

        /** 上架更新索引 **/
        if(status == HouseStatusEnum.PASSED.getValue()) {
            searchService.index(house.getId());
        } else {
            searchService.remove(house.getId());
        }
        return ServiceResult.ofResultEnum(ResultEnum.SUCCESS);
    }
    /**  **/
    private RentSearch validateSearchParam(RentSearch rentSearch) {
        //用于排序的字段
        String orderBy = rentSearch.getOrderBy();
        //升序还是降序
        String orderDirection = rentSearch.getOrderDirection();

        //默认是last_update_time进行排序
        if (orderBy == null) {
            rentSearch.setOrderBy("last_update_time");
        }

        if (rentSearch.getOrderBy() != null && rentSearch.getOrderBy().equals("lastUpdateTime")) {
            rentSearch.setOrderBy("last_update_time");
        } else if (rentSearch.getOrderBy() != null && rentSearch.getOrderBy().equals("distanceToSubway")) {
            rentSearch.setOrderBy("distance_to_subway");
        } else if (rentSearch.getOrderBy() != null && rentSearch.getOrderBy().equals("createTime")) {
            rentSearch.setOrderBy("create_time");
        }

        if (rentSearch.getRegionEnName() != null) {
            if (rentSearch.getRegionEnName().equals("*")) {
                rentSearch.setRegionEnName(null);
            }
        }
        if (rentSearch.getAreaBlock() != null) {
            if (rentSearch.getAreaBlock().equals("*")) {
                rentSearch.setAreaBlock(null);
            }
        }
        if (rentSearch.getPriceBlock() != null) {
            if (rentSearch.getPriceBlock().equals("*")) {
                rentSearch.setPriceBlock("null");
            }
        }
        if (rentSearch.getAreaBlock() != null) {
            if (rentSearch.getAreaBlock().equals("*-30")) {
                rentSearch.setAreaKey(1);
            } else if (rentSearch.getAreaBlock().equals("30-50")) {
                rentSearch.setAreaKey(2);
            } else if (rentSearch.getAreaBlock().equals("50-*")) {
                rentSearch.setAreaKey(3);
            }
        }

        if (rentSearch.getPriceBlock() != null) {
            if (rentSearch.getPriceBlock().equals("*-1000")) {
                rentSearch.setPriceKey(1);
            } else if (rentSearch.getPriceBlock().equals("1000-3000")) {
                rentSearch.setPriceKey(2);
            } else if (rentSearch.getPriceBlock().equals("3000-*")) {
                rentSearch.setPriceKey(3);
            }
        }
        return rentSearch;
    }

    @Override
    public int countAll() {
        return houseMapper.countAll();
    }

    /** 查询房源信息集 **/
    @Override
    public ServiceResult query(RentSearch rentSearch) {
        /** 如果有关键词就从es查询 **/
        if (rentSearch.getKeywords() != null && !rentSearch.getKeywords().isEmpty()) {
            /** 从es查到要查的id之后，从mysql中查全部 **/
            List<Long> houseIdList = searchService.esQuery(rentSearch);
            if (houseIdList.isEmpty() || houseIdList == null) {
                return ServiceResult.ofResultEnum(ResultEnum.ERROR_EMPTY_HOUSE);
            }
            /** 根据从es中查到的ID去mysql中查对应的数据 **/
            return mysqlQueryById(houseIdList);
        }
        return mysqlQuery(rentSearch);
    }

    /** 根据ID从mysql中去查 **/
    public ServiceResult mysqlQueryById(List<Long> houseIdList) {
        List<HouseDTO> houseDTOList = new ArrayList<>();

        List<House> houseList = houseMapper.findAllByIdList(houseIdList);
        Map<Long, HouseDTO> idToHouseMap = new HashMap<>();
        for (House house : houseList) {
            HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
            idToHouseMap.put(house.getId(), houseDTO);
        }

        //包装房屋细节 detail+tags
        FillHouseDetailAndTag(houseIdList, idToHouseMap);
        for (long houseId : houseIdList) {
            houseDTOList.add(idToHouseMap.get(houseId));
        }
        return ServiceResult.ofSuccess(houseDTOList);
    }

    /** 从mysql中去查 **/
    public ServiceResult mysqlQuery(RentSearch rentSearch) {
        RentSearch newRentSearch = validateSearchParam(rentSearch);
        List<HouseDTO> houseDtos = new ArrayList<>();
        int start = rentSearch.getStart();
        int length = rentSearch.getSize();
        int pageNum = start / length;
        List<House> houses = new ArrayList<>();
        //查询所有的房屋列表
        newRentSearch.setStart(pageNum);
        houses = houseMapper.userFindAllHousesBySearch(newRentSearch);
        if (houses.size() == 0) {
            return ServiceResult.ofResultEnum(ResultEnum.ERROR_EMPTY_HOUSE);
        }

        List<Long> houseIds = new ArrayList<>();
        Map<Long, HouseDTO> idToHouseMap = Maps.newHashMap();
        houses.forEach(item -> {
            HouseDTO houseDto = modelMapper.map(item, HouseDTO.class);
            houseDtos.add(houseDto);
            houseIds.add(item.getId());
            idToHouseMap.put(item.getId(), houseDto);
        });
        /*包装房屋细节*/
        FillHouseDetailAndTag(houseIds, idToHouseMap);
        return ServiceResult.ofSuccess(houseDtos);
    }



    /** 包装房屋细节 detail+tags **/
    private void FillHouseDetailAndTag(List<Long> houseIdList, Map<Long, HouseDTO> idToHouseMap) {
        /** 根据房屋id查询出所有的detail **/
        List<HouseDetail> houseDetailList = houseDetailMapper.findAllById(houseIdList);
        if(houseDetailList != null) {
            houseDetailList.forEach(houseDetail -> {
                HouseDTO houseDTO = idToHouseMap.get(houseDetail.getHouseId());
                HouseDetailDTO detailDTO = modelMapper.map(houseDetail, HouseDetailDTO.class);
                houseDTO.setHouseDetail(detailDTO);
            });
        }

        /** 根据房屋id找出所有的tag **/
        List<HouseTag> houseTagList = houseTagMapper.findAllByIdList(houseIdList);
        if (houseTagList != null) {
            houseTagList.forEach(houseTag -> {
                List<String> tags = new ArrayList<>();
                HouseDTO house = idToHouseMap.get(houseTag.getHouseId());
                tags.add(houseTag.getName());
                house.setTags(tags);
            });
        }
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
