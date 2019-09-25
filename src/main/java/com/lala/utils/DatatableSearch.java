package com.lala.utils;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author lala
 * dataTables搜索结构
 * int 类型是一定会传的，Integer代表有可能不传，如果为null就认定它为所有的状态码 */

@Data
public class DatatableSearch {

    /** Datatables要求回显的字段 **/
    private int draw;
    /** Datatables规定的分页字段 **/
    private int start;
    private int length;

    /** 自定义值 **/
    private Integer status;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createTimeMin;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createTimeMax;

    /** 城市筛选字段 **/
    private String city;
    /** 标题搜索字段 **/
    private String title;
    /** 排序字段 **/
    private String direction;
    /** 根据哪个字段来进行排序 **/
    private String orderBy;
}
