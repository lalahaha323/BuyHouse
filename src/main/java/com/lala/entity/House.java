package com.lala.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author lala
 */

@Data
public class House implements Serializable {
    private Long id;
    private String title;
    private int price;
    private int area;
    private int room;
    private int floor;
    private int totalFloor;
    private int watchTimes;
    private int buildYear;
    private int status;
    private Date createTime;
    private Date lastUpdateTime;
    private String cityEnName;
    private String regionEnName;
    private String cover;
    private int direction;
    private int distanceToSubway;
    private int parlour;
    private String district;
    private Long adminId;
    private int bathroom;
    private String street;
}
