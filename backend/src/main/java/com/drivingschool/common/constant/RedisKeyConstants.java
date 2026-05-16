package com.drivingschool.common.constant;

/**
 * Redis Key 常量
 * <p>
 * 统一管理 Redis Key 前缀，避免硬编码散落在业务代码中。
 * </p>
 */
public final class RedisKeyConstants {

    private RedisKeyConstants() {}

    /** 排班预约分布式锁：lock:schedule:{scheduleId} */
    public static final String LOCK_SCHEDULE = "lock:schedule:";

    /** 学员预约频率限制：limit:reservation:{studentId} */
    public static final String LIMIT_RESERVATION = "limit:reservation:";
}
