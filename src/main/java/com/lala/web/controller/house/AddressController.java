package com.lala.web.controller.house;

import com.lala.service.AddressService;
import com.lala.service.SubwayService;
import com.lala.service.result.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author lala
 */

@Controller
@RequestMapping("/address")
public class AddressController {

    @Autowired
    AddressService addressService;

    @Autowired
    SubwayService subwayService;

    /** 获取支持城市列表 **/
    @GetMapping("/support/cities")
    @ResponseBody
    public ServiceResult getSupportCities() {
        return addressService.findAllCities();
    }

    /** 获取对应城市支持区域列表 **/
    @GetMapping("/support/regions")
    @ResponseBody
    public ServiceResult getSupportRegions(@RequestParam(name = "city_name") String cityEnName) {
        return addressService.findAllRegionsByCityName(cityEnName);

    }

    /** 获取具体城市所支持的地铁线路 **/
    @GetMapping("/support/subway/line")
    @ResponseBody
    public ServiceResult getSupportSubwayLine(@RequestParam(name = "city_name") String cityEnName) {
        return subwayService.findAllSubwayByCityEnName(cityEnName);
    }
}
