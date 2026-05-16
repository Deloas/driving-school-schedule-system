package com.drivingschool.reservation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建预约请求 DTO
 * <p>
 * 只需传 scheduleId，学员信息和主教练从 Token 中获取，不由前端传。
 * </p>
 */
@Data
public class ReservationCreateDTO {

    @NotNull(message = "排班ID不能为空")
    private Long scheduleId;
}
