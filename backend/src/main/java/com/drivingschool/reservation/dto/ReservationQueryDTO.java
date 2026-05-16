package com.drivingschool.reservation.dto;

import lombok.Data;

import java.time.LocalDate;

/** 预约查询请求 DTO */
@Data
public class ReservationQueryDTO {
    private Integer page = 1;
    private Integer size = 10;
    /** 学员姓名模糊搜索 */
    private String studentName;
    /** 按教练筛选 */
    private Long coachId;
    private LocalDate reservationDate;
    private String timeSlot;
    private String status;
}
