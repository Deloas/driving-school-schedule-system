package com.drivingschool.common.enums;

import lombok.Getter;

/**
 * 车辆状态枚举
 * <p>
 * - NORMAL：正常可用，可以被排班绑定
 * - MAINTENANCE：维修中，不可被排班绑定
 * - STOPPED：长期停用，不可预约
 * </p>
 */
@Getter
public enum VehicleStatusEnum {

    /** 正常 — 可被排班使用 */
    NORMAL("NORMAL", "正常"),

    /** 维修中 — 暂时不可用 */
    MAINTENANCE("MAINTENANCE", "维修中"),

    /** 停用 — 长期不可用 */
    STOPPED("STOPPED", "停用");

    private final String code;
    private final String name;

    VehicleStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
