package com.drivingschool.common.enums;

import lombok.Getter;

/**
 * 预约状态枚举
 * <p>
 * 预约的生命周期：SUCCESS → (CANCELLED | COMPLETED | ABSENT)
 * - SUCCESS：预约成功，待练车
 * - CANCELLED：学员主动取消
 * - COMPLETED：教练确认完成练车
 * - ABSENT：学员爽约未到场
 * - REJECTED：被管理员取消或拒绝
 * </p>
 */
@Getter
public enum ReservationStatusEnum {

    /** 预约成功（待练车） */
    SUCCESS("SUCCESS", "预约成功"),

    /** 已取消 */
    CANCELLED("CANCELLED", "已取消"),

    /** 已完成练车 */
    COMPLETED("COMPLETED", "已完成"),

    /** 爽约（未到场） */
    ABSENT("ABSENT", "爽约"),

    /** 被管理员拒绝/取消 */
    REJECTED("REJECTED", "已拒绝");

    private final String code;
    private final String name;

    ReservationStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
