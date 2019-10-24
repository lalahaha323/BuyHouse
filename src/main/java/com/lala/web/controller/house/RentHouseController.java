package com.lala.web.controller.house;

import com.lala.elasticsearch.RentSearch;
import com.lala.elasticsearch.RentValueBlock;
import com.lala.enums.ResultEnum;
import com.lala.service.AddressService;
import com.lala.service.HouseService;
import com.lala.service.result.ServiceResult;
import com.lala.web.dto.SupportAddressDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

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
}
