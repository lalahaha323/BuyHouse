package com.lala.web.controller.admin;

import com.lala.enums.ResultEnum;
import com.lala.service.AliyunService;
import com.lala.service.result.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 后台管理控制，所有后台的请求都在这里进行处理
 * @author lala
 */

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    AliyunService aliyunService;

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

    /** 上传图片 **/
    @PostMapping(value = "/upload/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ServiceResult uploadPhoto(@RequestParam("file") MultipartFile file) {
        if(file.isEmpty()) {
            return ServiceResult.ofResultEnum(ResultEnum.ERROR_EMPTY_PICTURE);
        }
        return aliyunService.uploadFile(file);
    }

}
