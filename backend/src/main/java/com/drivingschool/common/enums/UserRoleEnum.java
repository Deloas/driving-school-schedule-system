package com.drivingschool.common.enums;

import lombok.Getter;

/**
 * 用户角色枚举
 * <p>
 * 系统包含三类角色，不同角色登录后看到的页面和可操作的功能完全不同。
 * </p>
 */
@Getter
public enum UserRoleEnum {

    /** 管理员 — 拥有系统全部权限 */
    ADMIN("ADMIN", "管理员"),

    /** 教练 — 查看排班、预约名单，确认完成练车 */
    COACH("COACH", "教练"),

    /** 学员 — 预约练车，查看个人记录 */
    STUDENT("STUDENT", "学员");

    /** 数据库中存储的角色值 */
    private final String code;

    /** 角色中文名称 */
    private final String name;

    UserRoleEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
