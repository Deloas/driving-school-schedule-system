package com.drivingschool.schedule.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 排班查询请求 DTO
 */
@Data
public class ScheduleQueryDTO {
    private Integer page = 1;
    private Integer size = 10;

    private Long coachId;
    private Long vehicleId;
    private LocalDate scheduleDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String timeSlot;
    private String status;
}
