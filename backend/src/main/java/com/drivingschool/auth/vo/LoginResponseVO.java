package com.drivingschool.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 登录响应 VO
 */
@Data
@Builder
@AllArgsConstructor
public class LoginResponseVO {

    /** JWT Token，前端后续请求需携带此 Token */
    private String token;

    /** 用户ID */
    private Long userId;

    /** 登录账号 */
    private String username;

    /** 角色：ADMIN / COACH / STUDENT */
    private String role;

    /** 关联业务ID（学员ID 或 教练ID），管理员为 null */
    private Long relatedId;
}
