package com.drivingschool.user.dto;

import lombok.Data;

/** 账号查询请求 */
@Data
public class UserQueryDTO {
    private Integer page = 1;
    private Integer size = 10;
    private String keyword;
    private String role;
    private String status;
}
