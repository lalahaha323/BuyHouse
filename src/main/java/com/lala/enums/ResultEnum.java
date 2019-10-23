package com.lala.enums;

import lombok.Getter;

/**
 * 结果响应枚举类
 * @author lala
 */
@Getter
public enum ResultEnum {

    //成功请求
    SUCCESS(200, "ok"),
    //错误请求
    BAD_REQUEST(400, "Bad Request"),
    //没有发现资源
    NOT_FOUND(404, "Not Found"),
    //服务器错误
    INTERNAL_SERVER_ERROR(500, "Unknown Internal Error"),
    //无效的参数
    NOT_VALID_PARAM(40005, "Not valid Params"),
    //操作不支持
    NOT_SUPPORTED_OPERATION(40006, "Operation not supported"),
    //未登录
    NOT_LOGIN(50000, "Not Login"),

    //图片问题
    //图片上传成功
    SUCCESS_UPLOAD_PICTURE(60000, "图片上传成功"),
    //MultiFile转换file失败
    ERROR_FORMATTER(61000, "转换失败"),
    //图片为空
    ERROR_EMPTY_PICTURE(61001, "图片为空"),
    ERROR_UPLOAD_PICTURE(61002, "图片上传失败"),

    //线路问题
    ERROR_EMPTY_SUBWAY(70000, "没有地铁线路"),
    ERROR_EMPTY_SUBWAYSTATION(70001, "没有地铁站"),
    //数据问题
    ERROR_EMPTY_HOUSE(71000, "房屋不存在"),
    ERROR_EMPTY_HOUSEDETAIL(71001, "房屋详情不存在"),
    ERROR_EMPTY_HOUSETAG(71002, "房屋标签不存在"),
    ERROR_EXIST_HOUSETAG(71003, "房屋标签已经存在"),
    ERROR_DATA(71004, "数据有问题"),
    ERROR_EMPTY_DATA(71005, "修改类型为空"),
    ERROR_MISMATCH_EMAIL(71006, "不支持的邮箱格式"),
    ERROR_NO_FILED(71007, "不支持的属性"),
    ERROR_STATUS_NOCHANGE(71008, "状态没有发生变化"),
    ERROR_STATUS_NORENT(71009, "已出租的房源不允许修改状态"),
    ERROR_STATUS_NODELETE(71010, "已删除的房源不允许操作")
    ;

    private Integer code;
    private String message;
    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
