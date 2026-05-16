package com.drivingschool.common.enums;

import lombok.Getter;

/**
 * 排班状态枚举
 * <p>
 * 控制教练某日期某时间段的预约开放状态：
 * OPEN — 可以预约；
 * CLOSED — 管理员关闭；
 * CANCELLED — 排班被取消。
 * 当 current_students >= max_students 时，前端应展示为"已满员"，
 * 但数据库状态仍为 OPEN，因为有人取消后会释放名额。
 * </p>
 */
@Getter
public enum ScheduleStatusEnum {

    /** 可预约 */
    OPEN("OPEN", "可预约"),

    /** 停止预约（管理员手动关闭） */
    CLOSED("CLOSED", "停止预约"),

    /** 排班取消 */
    CANCELLED("CANCELLED", "已取消");

    private final String code;
    private final String name;

    ScheduleStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
