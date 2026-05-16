package com.drivingschool.reservation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建预约请求 DTO
 * <p>
 * adjusted=false → 预约主教练（schedule.coach_id 必须等于学员主教练）
 * adjusted=true  → 调剂预约（schedule 为推荐的可调剂排班）
 * </p>
 */
@Data
public class ReservationCreateDTO {

    @NotNull(message = "排班ID不能为空")
    private Long scheduleId;

    /** 是否调剂预约，默认 false（使用 boolean 原始类型，避免反序列化空指针） */
    private boolean adjusted = false;

    /** 调剂原因（adjusted=true 时必填） */
    private String adjustReason;
}
