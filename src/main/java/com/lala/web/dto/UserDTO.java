package com.lala.web.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author lala
 */
@Data
public class UserDTO {
    private Long id;
    private String name;
    private String avatar;
    private String phoneNumber;
    private Date lastLoginTime;
}
