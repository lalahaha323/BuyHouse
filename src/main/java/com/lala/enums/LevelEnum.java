package com.lala.enums;

import com.lala.entity.SupportAddress;
import lombok.Getter;

/**
 * @author lala
 */
@Getter
public enum  LevelEnum {
    CITY("city"),
    REGION("region");

    private String value;
    LevelEnum(String value) {
        this.value = value;
    }
}
