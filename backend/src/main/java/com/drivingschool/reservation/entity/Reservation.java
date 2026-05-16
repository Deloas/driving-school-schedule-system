package com.drivingschool.reservation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 预约实体 — 对应 reservation 表
 * <p>
 * main_coach_id 存学员归属教练，actual_coach_id 存本次实际带练教练。
 * M5 阶段两者相同（只允许预约主教练），is_adjusted=0。
 * </p>
 */
@Data
@TableName("reservation")
public class Reservation {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 学员ID */
    private Long studentId;

    /** 学员主教练ID */
    private Long mainCoachId;

    /** 本次实际带练教练ID */
    private Long actualCoachId;

    /** 本次预约车辆ID */
    private Long vehicleId;

    /** 排班ID */
    private Long scheduleId;

    /** 预约日期 */
    private LocalDate reservationDate;

    /** 时间段 */
    private String timeSlot;

    /** 状态：SUCCESS / CANCELLED / COMPLETED / ABSENT */
    private String status;

    /** 是否调剂：0否 1是 */
    private Integer isAdjusted;

    /** 调剂原因 */
    private String adjustReason;

    /** 取消原因 */
    private String cancelReason;

    private LocalDateTime createTime;
    private LocalDateTime cancelTime;
    private LocalDateTime updateTime;
}
