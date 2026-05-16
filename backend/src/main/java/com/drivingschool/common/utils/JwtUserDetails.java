package com.drivingschool.common.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JWT Token 中存储的用户信息
 * <p>
 * 不直接实现 Spring Security 的 UserDetails 接口，
 * 而是作为轻量级数据载体存入 SecurityContext.principal 中。
 * 这样可以避免从数据库加载权限列表，减少查询开销。
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtUserDetails {

    /** 用户ID（sys_user.id） */
    private Long userId;

    /** 登录账号 */
    private String username;

    /** 角色：ADMIN / COACH / STUDENT */
    private String role;

    /** 关联业务ID（学员ID 或 教练ID） */
    private Long relatedId;
}
