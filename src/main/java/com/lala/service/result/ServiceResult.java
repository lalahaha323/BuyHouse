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

    public ServiceResult() {
        this.code = ResultEnum.SUCCESS.getCode();
        this.message = ResultEnum.SUCCESS.getMessage();
    }

    public ServiceResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static ServiceResult ofMessage(int code, String message) {
        return new ServiceResult(code, message, null);
    }

    public static ServiceResult ofSuccess(Object data) {
        return new ServiceResult(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMessage(), data);
    }

    public static ServiceResult ofResultEnum(ResultEnum resultEnum) {
        return new ServiceResult(resultEnum.getCode(), resultEnum.getMessage(), null);
    }
}
