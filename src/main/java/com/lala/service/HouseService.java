package com.lala.service;

import com.lala.elasticsearch.MapSearch;
import com.lala.elasticsearch.RentSearch;
import com.lala.service.result.ResultDataTableResponse;
import com.lala.service.result.ServiceResult;
import com.lala.utils.DatatableSearch;
import com.lala.web.Form.HouseForm;
import com.lala.web.dto.HouseSubscribeDTO;


import java.util.List;

/**
 * 房源管理服务接口
 * @author lala
 */
public interface HouseService {
    /**　新增房源 **/
    ServiceResult save(HouseForm houseForm);
    /** 查询 **/
    ResultDataTableResponse findAll(DatatableSearch datatableSearch);
    /** 查询房屋详情 detail+tag+pictures**/
    ServiceResult findAllOne(Long id);
    /** 修改房屋 **/
    ServiceResult update(HouseForm houseForm);
    /** 修改房屋状态 **/
    ServiceResult updateStatus(Long id, int status);
    /** 查询房源信息集 **/
    ServiceResult query(RentSearch rentSearch);
    /** 查询房源个数 **/
    int countAll();
    /** 全地图查询 **/
    ServiceResult wholeMapQuery(MapSearch mapSearch);
    /** 缩放地图查询 **/
    ServiceResult boundMapQuery(MapSearch mapSearch);
    /** 管理员查看用户预约记录 **/
    ResultDataTableResponse adminFindSubscribeList(int draw, int start, int size);

}
