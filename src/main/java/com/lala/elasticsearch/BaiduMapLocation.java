package com.lala.elasticsearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 百度位置信息
 * @author lala
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaiduMapLocation {

    @JsonProperty("lon")
    private double lon;//经度
    @JsonProperty("lat")
    private double lat;//维度
}
