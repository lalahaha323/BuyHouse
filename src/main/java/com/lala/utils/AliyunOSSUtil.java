package com.lala.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.lala.config.AliyunOssConfig;
import com.lala.enums.ResultEnum;
import com.lala.service.result.ServiceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;


/**
 * @author lala
 */

@Component
public class AliyunOSSUtil {

    private static final Logger log = LoggerFactory.getLogger(AliyunOSSUtil.class);

    @Autowired
    AliyunOssConfig aliyunOssConfig;

    /** 上传文件 **/
    public ServiceResult upload(File file) {
        log.info("----OSS文件上传开始----" + file.getName());

        String endpoint = aliyunOssConfig.getEndpoint();
        String accessKeyId = aliyunOssConfig.getAccessKeyId();
        String accessKeySecret = aliyunOssConfig.getAccessKeySecret();

        OSS client = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        String fileUrl = aliyunOssConfig.getFiledir() + file.getName();
        // 上传文件
        PutObjectResult result = client.putObject(new PutObjectRequest(aliyunOssConfig.getBucketName(), fileUrl, new ByteArrayInputStream("lala".getBytes())));
        if (result != null) {
            log.info("------OSS文件上传成功------" + fileUrl);
            return ServiceResult.ofResultEnum(ResultEnum.SUCCESS_UPLOAD_PICTURE);
        }
        return ServiceResult.ofResultEnum(ResultEnum.ERROR_UPLOAD_PICTURE);
    }
}
