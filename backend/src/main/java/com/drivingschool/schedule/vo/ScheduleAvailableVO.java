package com.drivingschool.schedule.vo;

import lombok.Data;

/**
 * 可预约排班 VO — 供 M5 学员预约页使用
 */
@Data
public class ScheduleAvailableVO {
    private Long scheduleId;
    private Long coachId;
    private String coachName;
    private Long vehicleId;
    private String plateNumber;
    private Integer maxStudents;
    private Integer currentStudents;
    private Integer remainCount;
    private String status;
}
