package com.lala.service;

import com.lala.service.result.ServiceResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;

/**
 * aliyun服务
 * @author lala
 */
public interface AliyunService {

     ServiceResult uploadFile(MultipartFile file);
}
