package com.lala.elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lala
 * 自动补全
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HouseFix {
    private String input;
    /** 默认权重 **/
    private int weight = 10;
}
