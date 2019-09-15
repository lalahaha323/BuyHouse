package com.lala.entity;

import lombok.Data;

/**
 * @author lala
 */

@Data
public class HousePicture {
    private Long id;
    private Long houseId;
    private String path;
    private String cdnPrefix;
    private int width;
    private int height;
    private String location;

}
