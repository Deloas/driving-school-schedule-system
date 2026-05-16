package com.drivingschool.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 当前用户信息响应 VO — 用于 GET /api/auth/me
 */
@Data
@Builder
@AllArgsConstructor
public class UserInfoVO {

    /** 用户ID */
    private Long userId;

    /** 登录账号 */
    private String username;

    /** 角色 */
    private String role;

    /** 关联业务ID */
    private Long relatedId;

    /** 账号状态 */
    private String status;
}
