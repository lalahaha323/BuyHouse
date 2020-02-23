package com.lala.enums;

import lombok.Getter;

/**
 * 房屋预约状态描述
 * @author lala
 */

@Getter
public enum HouseSubscribeStatusEnum {
    NO_SUBSCRIBE(0),//未预约
    IN_ORDER_LIST(1),//已加入待看清单
    IN_ORDER_TIME(2),//已经预约看房时间
    FINISH(3)//已经完成预约
    ;

    private int value;
    HouseSubscribeStatusEnum(int value) {
        this.value = value;
    }

    public static HouseSubscribeStatusEnum getStatus(int value) {
        for (HouseSubscribeStatusEnum status : HouseSubscribeStatusEnum.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        return HouseSubscribeStatusEnum.NO_SUBSCRIBE;
    }
}
