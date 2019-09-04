package com.lala.entity;

import lombok.Data;

/**
 * @author lala
 */
@Data
public class SupportAddress {
    private Long id;
    private String belongTo;
    private String enName;
    private String cnName;
    private String level;
}
