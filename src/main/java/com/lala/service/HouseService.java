package com.lala.service;

import com.lala.service.result.ResultDataTableResponse;
import com.lala.service.result.ServiceResult;
import com.lala.utils.DatatableSearch;
import com.lala.web.Form.HouseForm;


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
}
