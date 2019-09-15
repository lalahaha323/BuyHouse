package com.lala.entity;

import lombok.Data;

/**
 * @author lala
 */

@Data
public class HouseTag {
    private Long id;
    private Long houseId;
    private String name;

    public HouseTag(Long houseId, String name) {
        this.houseId = houseId;
        this.name = name;
    }
}
