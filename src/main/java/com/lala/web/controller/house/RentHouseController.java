package com.lala.web.controller.house;

import com.lala.elasticsearch.RentSearch;
import com.lala.elasticsearch.RentValueBlock;
import com.lala.entity.SupportAddress;
import com.lala.enums.ResultEnum;
import com.lala.service.AddressService;
import com.lala.service.HouseService;
import com.lala.service.SearchService;
import com.lala.service.UserService;
import com.lala.service.result.ServiceResult;
import com.lala.web.dto.HouseDTO;
import com.lala.web.dto.SupportAddressDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;

/**
 * @author lala
 */

@Controller
@RequestMapping("/rent")
public class RentHouseController {

    @Autowired
    AddressService addressService;
    @Autowired
    HouseService houseService;
    @Autowired
    UserService userService;
    @Autowired
    SearchService searchService;

    @GetMapping("/house")
    public String rentHousePage(@ModelAttribute RentSearch rentSearch,
                                Model model,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        /** 如果没有选中城市【因为你要根据城市去选择租房，所以一定要选择城市】 **/
        if (rentSearch.getCityEnName() == null) {
            String cityEnNameInSession = (String) session.getAttribute("cityEnName");
            if (cityEnNameInSession == null) {
                redirectAttributes.addAttribute("msg", "must_chose_city");
                return "redirect:/index";
            } else {
                rentSearch.setCityEnName(cityEnNameInSession);
            }
        } else {
            session.setAttribute("cityEnName", rentSearch.getCityEnName());
        }

        SupportAddressDTO city =  addressService.getCityByCityEnNameAndLevel(rentSearch.getCityEnName());
        model.addAttribute("currentCity", city);
        ServiceResult result = addressService.findAllRegionsByCityName(rentSearch.getCityEnName());
        if (result.getCode() == ResultEnum.NOT_FOUND.getCode()) {
            redirectAttributes.addAttribute("msg", "must_choose_city");
            return "redirect:/index";
        } else {
            model.addAttribute("regions", result.getData());
        }
        /** 如果没有选择区域，就说明该城市的所有区域用户都想看 **/
        if (rentSearch.getRegionEnName() == null) {
            rentSearch.setRegionEnName("*");
        }
        ServiceResult query = houseService.query(rentSearch);
        int querySize = houseService.countAll();
        model.addAttribute("total", querySize);
        model.addAttribute("houses", query.getData());


        model.addAttribute("searchBody", rentSearch);
        model.addAttribute("priceBlocks", RentValueBlock.PRICE_BLOCK);
        model.addAttribute("areaBlocks", RentValueBlock.AREA_BLOCK);
        model.addAttribute("currentPriceBlock", RentValueBlock.matchPrice(rentSearch.getPriceBlock()));
        model.addAttribute("currentAreaBlock", RentValueBlock.matchArea(rentSearch.getAreaBlock()));
        return "rent-list";

    }

    /** 房屋详情页面　**/
    @GetMapping("/house/show/{id}")
    public String show(@PathVariable(value = "id") Long houseId, Model model) {
        if (houseId <= 0) {
            return "404";
        }

        ServiceResult result = houseService.findAllOne(houseId);
        if (result.getCode() != 200) {
            return "404";
        }
        HouseDTO house = (HouseDTO) result.getData();
        String cityEnName = house.getCityEnName();
        String regionEnName = house.getRegionEnName();
        String district = house.getDistrict();

        SupportAddressDTO city = addressService.getCityByCityEnNameAndLevel(cityEnName);
        SupportAddressDTO region = addressService.getRegionByEnNameAndBelongTo(regionEnName, cityEnName);
        model.addAttribute("city", city);
        model.addAttribute("region", region);
        model.addAttribute("house", house);

        ServiceResult userResult = userService.getUserById(house.getAdminId());
        model.addAttribute("agent", userResult.getData());
        return "house-detail";

    }

    /** 自动补全接口 **/
    @GetMapping("/house/autocomplete")
    @ResponseBody
    public ServiceResult autoComplete(@RequestParam(value = "prefix") String prefix) {
        if (prefix.isEmpty()) {
            return ServiceResult.ofResultEnum(ResultEnum.BAD_REQUEST);
        }
        return searchService.fix(prefix);
    }
}
