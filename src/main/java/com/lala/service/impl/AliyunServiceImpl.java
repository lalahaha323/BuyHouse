package com.lala.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.lala.config.AliyunOssConfig;
import com.lala.enums.ResultEnum;
import com.lala.service.AliyunService;
import com.lala.service.result.ServiceResult;
import com.lala.utils.AliyunOSSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author lala
 */

@Service
public class AliyunServiceImpl implements AliyunService {

    @Autowired
    AliyunOSSUtil aliyunOSSUtil;

    @Override
    public ServiceResult uploadFile(MultipartFile file) {
        File newFile = new File(file.getOriginalFilename());
        try {
            file.transferTo(newFile);
            return aliyunOSSUtil.upload(newFile);
        } catch (IOException e) {
            return ServiceResult.ofResultEnum(ResultEnum.ERROR_FORMATTER);
        }
    }
}
