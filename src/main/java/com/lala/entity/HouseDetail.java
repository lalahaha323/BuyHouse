package com.lala.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lala
 */
@Data
public class HouseDetail implements Serializable {
    private Long id;
    private Long houseId;
    private String description;
    private String layoutDesc;
    private String traffic;
    private String roundService;
    private int rentWay;
    private String address;
    private Long subwayLineId;
    private Long subwayStationId;
    private String subwayLineName;
    private String subwayStationName;
}
