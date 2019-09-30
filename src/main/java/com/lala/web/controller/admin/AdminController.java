package com.lala.web.controller.admin;

import com.lala.entity.SubwayStation;
import com.lala.entity.SupportAddress;
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
}
