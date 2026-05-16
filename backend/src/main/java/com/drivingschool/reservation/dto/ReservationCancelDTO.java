package com.drivingschool.reservation.dto;

import lombok.Data;

/** 取消预约请求 DTO */
@Data
public class ReservationCancelDTO {
    /** 取消原因 */
    private String reason;
}
