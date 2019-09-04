package com.lala.web.controller.house;

import com.lala.service.AddressService;
import com.lala.service.result.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author lala
 */

@Controller
@RequestMapping("/address")
public class AddressController {

    @Autowired
    AddressService addressService;

    /** 获取支持城市列表 **/
    @GetMapping("/support/cities")
    @ResponseBody
    public ServiceResult getSupportCities() {
        return addressService.findAllCities();
    }
}
