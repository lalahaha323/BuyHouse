package com.lala.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lala
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HouseBucketDTO {
    private String key;
    private long count;
}
