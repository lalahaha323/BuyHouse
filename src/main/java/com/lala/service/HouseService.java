package com.lala.service;

import com.lala.service.result.ResultDataTableResponse;
import com.lala.service.result.ServiceResult;
import com.lala.utils.DatatableSearch;
import com.lala.web.dto.HouseDTO;
import com.lala.web.form.HouseForm;

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
}
