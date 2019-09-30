package com.lala.service;

import com.lala.service.result.ServiceResult;

/**
 * @author lala
 */
public interface HouseTagService {

    /** 删除房屋标签 **/
    ServiceResult deleteTagsByHouseIdAndTag(Long id, String tag);
}
