package com.lala.elasticsearch;

import lombok.Data;

/**
 * @author lala
 * 从百度地图的正逆地理编码开发文档中的
 */
public class BaiduAPI {
    //根据城市查询坐标
    public static final String BAIDU_MAP_GEOCONV_API = "http://api.map.baidu.com/geocoding/v3/?";
    /**
     * POI数据管理接口
     */
    //创建数据(create poi)接口
    public static final String CREATEPOI = "http://api.map.baidu.com/geodata/v3/poi/create";
    //查询指定条件的数据（poi）列表接口
    public static final String LISTPOI = "http://api.map.baidu.com/geodata/v3/poi/list?";
    //查询指定id的数据（poi）详情接口
    public static final String DETAILPOI = "http://api.map.baidu.com/geodata/v3/poi/detail?";
    //修改数据（poi）接口
    public static final String UPDATEPOI = "http://api.map.baidu.com/geodata/v3/poi/update";
    //删除数据（poi）接口（支持批量）
    public static final String DELETEPOI = "http://api.map.baidu.com/geodata/v3/poi/delete";
    //记录关联的geotable的标识
    public static final String GEOTABLE_ID = "209092";
}
