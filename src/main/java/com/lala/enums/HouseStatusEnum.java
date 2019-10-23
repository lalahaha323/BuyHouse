package com.lala.enums;

import lombok.Data;
import lombok.Getter;

/**
 * @author lala
 */

@Getter
public enum  HouseStatusEnum {
    UNCHECKED(0),//未审核
    PASSED(1),//审核通过
    RENTED(2),//已出租
    DELETED(3)//逻辑删除
    ;

    private int value;

    HouseStatusEnum(int value) {
        this.value = value;
    }
}
