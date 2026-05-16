package com.drivingschool.common.enums;

import lombok.Getter;

/**
 * 用户状态枚举
 * <p>
 * 用于 sys_user 表的 status 字段，控制账号启用/禁用。
 * </p>
 */
@Getter
public enum UserStatusEnum {

    /** 正常 — 账号可用 */
    ENABLE("ENABLE", "正常"),

    /** 禁用 — 账号不可登录 */
    DISABLE("DISABLE", "禁用");

    private final String code;
    private final String name;

    UserStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
