package com.lala.service;

import com.lala.web.form.HouseForm;

/**
 * 房源管理服务接口
 * @author lala
 */
public interface HouseService {
    /**　新增房源 **/
    void save(HouseForm houseForm);
}
