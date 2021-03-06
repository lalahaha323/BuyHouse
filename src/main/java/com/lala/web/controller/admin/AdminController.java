package com.lala.web.controller.admin;

import com.google.common.base.Strings;
import com.lala.enums.HouseOperation;
import com.lala.enums.HouseStatusEnum;
import com.lala.enums.LevelEnum;
import com.lala.enums.ResultEnum;
import com.lala.service.*;
import com.lala.service.result.ResultDataTableResponse;
import com.lala.service.result.ServiceResult;
import com.lala.utils.DatatableSearch;
import com.lala.web.Form.HouseForm;
import com.lala.web.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 后台管理控制，所有后台的请求都在这里进行处理
 * @author lala
 */

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    AliyunService aliyunService;
    @Autowired
    SupportAddressService supportAddressService;
    @Autowired
    HouseService houseService;
    @Autowired
    SubwayService subwayService;
    @Autowired
    SubwayStationService subwayStationService;
    @Autowired
    HouseTagService houseTagService;

    /** 后台管理中心 **/
    @GetMapping("/center")
    public String adminCenterPage() {
        return "admin/center";
    }

    /** 欢迎页 **/
    @GetMapping("/welcome")
    public String welcomePage() {
        return "admin/welcome";
    }

    /** 管理员登录页 **/
    @GetMapping("/login")
    public String adminLoginPage() {
        return "admin/login";
    }

    /** 房源新增功能页 **/
    @GetMapping("/add/house")
    public String adminHousePage() {
        return "admin/house-add";
    }

    /** 房源列表页 **/
    @GetMapping("/house/list")
    public String houseListPage() {
        return "admin/house-list";
    }

    /** 房源详细描述页 **/
    @GetMapping("/house/subscribe")
    public String houseSubscribe() {
        return "admin/subscribe";
    }

    /** 房源预约管理 **/
    @GetMapping("/house/subscribe/list")
    @ResponseBody
    public ResultDataTableResponse houseSubscribeList(@RequestParam(value = "draw") int draw,
                                            @RequestParam(value = "start") int start,
                                            @RequestParam(value = "length") int size) {
        return houseService.adminFindSubscribeList(draw, start, size);
    }

    /** 上传图片 **/
    @PostMapping(value = "/upload/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ServiceResult uploadPhoto(@RequestParam("file") MultipartFile file) {
        if(file.isEmpty()) {
            return ServiceResult.ofResultEnum(ResultEnum.ERROR_EMPTY_PICTURE);
        }
        return aliyunService.uploadFile(file);
    }

    /** 新增房源 **/
    @PostMapping("/add/house")
    @ResponseBody
    public ServiceResult addHouse(@Valid @ModelAttribute("form-house-add") HouseForm houseForm, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return ServiceResult.ofResultEnum(ResultEnum.BAD_REQUEST);
        }
        if(houseForm.getPhotos() == null || houseForm.getCover() == null) {
            return ServiceResult.ofMessage(ResultEnum.BAD_REQUEST.getCode(), "必须上传照片");
        }
        Map<LevelEnum, SupportAddressDTO> map = supportAddressService.findCityAndRegionByCNameAndRName(houseForm.getCityEnName(), houseForm.getRegionEnName());
        if (map.keySet().size() != 2) {
            return ServiceResult.ofResultEnum(ResultEnum.NOT_VALID_PARAM);
        }
        return houseService.save(houseForm);
    }

    /** 分页显示所有房源,因为前端使用的是datatables控件，具有规定的数据格式 **/
    @PostMapping("/houses")
    @ResponseBody
    public ResultDataTableResponse houses(@ModelAttribute DatatableSearch searchBody) {

        if (searchBody.getOrderBy().equals("createTime")) {
            searchBody.setOrderBy("create_time");
        } else if (searchBody.getOrderBy().equals("watchTimes")) {
            searchBody.setOrderBy("watch_times");
        }

        return houseService.findAll(searchBody);
    }

    /** 房源编辑页 某个房源要编辑，这个id就是这个房源的id**/
    @GetMapping("/house/edit")
    public String houseEditPage(@RequestParam(value = "id") Long id, Model model) {
        if(id == null || id < 1) {
            return "404";
        }

        ServiceResult<HouseDTO> serviceResult = houseService.findAllOne(id);
        if(serviceResult.getCode() != 200) {
            return "404";
        }
        HouseDTO houseDTO = serviceResult.getData();
        String cityEnName = houseDTO.getCityEnName();
        String regionEnName = houseDTO.getRegionEnName();
        SupportAddressDTO cityDTO = supportAddressService.findCityByCityEnName(cityEnName);
        SupportAddressDTO regionDTO = supportAddressService.findRegionByRegionEnName(regionEnName);

        HouseDetailDTO houseDetailDTO = houseDTO.getHouseDetail();
        SubwayDTO subwayDTO = subwayService.findSubwayBySubwayLineId(houseDetailDTO.getSubwayLineId());
        SubwayStationDTO subwayStationDTO = subwayStationService.findBySubwayStationId(houseDetailDTO.getSubwayStationId());

        model.addAttribute("house", houseDTO);
        model.addAttribute("city", cityDTO);
        model.addAttribute("region", regionDTO);
        model.addAttribute("subway", subwayDTO);
        model.addAttribute("station", subwayStationDTO);

        return "admin/house-edit";
    }

    /** 编辑接口 **/
    @PostMapping("/house/edit")
    @ResponseBody
    public ServiceResult saveHouse(@ModelAttribute("form-house-edit") HouseForm houseForm) {
        return houseService.update(houseForm);
    }

    /** 移除标签接口 **/
    @DeleteMapping("/house/tag")
    @ResponseBody
    public ServiceResult removeHouseTag(@RequestParam(value = "house_id") Long houseId,
                                        @RequestParam(value = "tag") String tag) {
        if (houseId < 1 || Strings.isNullOrEmpty(tag)) {
            return ServiceResult.ofResultEnum(ResultEnum.ERROR_DATA);
        }
        return houseTagService.deleteTagsByHouseIdAndTag(houseId, tag);
    }

    /** 增加标签接口 **/
    @PostMapping("/house/tag")
    @ResponseBody
    public ServiceResult addHouseTag(@RequestParam(value = "house_id") Long houseId,
                                     @RequestParam(value = "tag") String tag) {
        if (houseId < 1 || Strings.isNullOrEmpty(tag)) {
            return ServiceResult.ofResultEnum(ResultEnum.ERROR_DATA);
        }
        return houseTagService.addTagByHouseIdAndTag(houseId, tag);
    }

    /** 增加审核接口 **/
    @PutMapping("/house/operate/{id}/{operation}")
    @ResponseBody
    public ServiceResult HouseOperate(@PathVariable(value = "id") Long id,
                                      @PathVariable(value = "operation") int operation) {
        if (id <= 0) {
            return ServiceResult.ofResultEnum(ResultEnum.ERROR_EMPTY_HOUSE);
        }
        switch (operation) {
            case HouseOperation.PASS://通过审核
                return houseService.updateStatus(id, HouseStatusEnum.PASSED.getValue());//更新状态为通过审核
            case HouseOperation.PULL_OUT://下架重新审核
                return houseService.updateStatus(id, HouseStatusEnum.UNCHECKED.getValue());//更新状态未审核
            case HouseOperation.DELETE://逻辑删除
                return houseService.updateStatus(id, HouseStatusEnum.DELETED.getValue());//更新状态为逻辑删除和
            case HouseOperation.RENT://出租
                return houseService.updateStatus(id, HouseStatusEnum.RENTED.getValue());//更新状态为出租
            default:
                return ServiceResult.ofResultEnum(ResultEnum.NOT_VALID_PARAM);
        }
    }
}
