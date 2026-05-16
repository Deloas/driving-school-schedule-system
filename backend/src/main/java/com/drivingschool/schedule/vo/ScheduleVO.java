package com.drivingschool.schedule.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 排班详情响应 VO
 */
@Data
public class ScheduleVO {
    private Long id;
    private Long coachId;
    private String coachName;
    private Long vehicleId;
    private String plateNumber;
    private LocalDate scheduleDate;
    private String timeSlot;
    private Integer maxStudents;
    private Integer currentStudents;
    /** 剩余名额 */
    private Integer remainCount;
    private String status;
    private String remark;
    private LocalDateTime createTime;
}
