package com.drivingschool.common.enums;

import lombok.Getter;

/**
 * 练车时间段枚举 — 系统采用半天粒度
 * <p>
 * 后端只存 MORNING / AFTERNOON，避免"全天"带来的复杂冲突。
 * 前端如果展示"全天预约"，应拆分为上午和下午两个时间段分别提交。
 * </p>
 */
@Getter
public enum TimeSlotEnum {

    /** 上午 */
    MORNING("MORNING", "上午"),

    /** 下午 */
    AFTERNOON("AFTERNOON", "下午");

    private final String code;
    private final String name;

    TimeSlotEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
