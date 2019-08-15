package com.lala.service.result;

import com.lala.enums.ResultEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lala
 */

@Data
public class ServiceResult<T> implements Serializable {

    private int code;
    private String message;
    private T data;

    private ServiceResult() {

    }

    public static <T> ServiceResult<T> returnResult(ResultEnum resultEnum, T data) {

        ServiceResult<T> serviceResult = new ServiceResult<>();

        serviceResult.code = resultEnum.getCode();
        serviceResult.message = resultEnum.getMessage();
        serviceResult.data = data;

        return serviceResult;
    }
}
