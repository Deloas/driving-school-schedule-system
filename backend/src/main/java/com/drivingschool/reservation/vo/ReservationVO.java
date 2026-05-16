package com.drivingschool.reservation.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** 预约详情响应 VO */
@Data
public class ReservationVO {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentPhone;
    private Long mainCoachId;
    private String mainCoachName;
    private Long actualCoachId;
    private String actualCoachName;
    private Long vehicleId;
    private String plateNumber;
    private Long scheduleId;
    private LocalDate reservationDate;
    private String timeSlot;
    private String status;
    private Integer isAdjusted;
    private String adjustReason;
    private String cancelReason;
    private LocalDateTime createTime;
    private LocalDateTime cancelTime;
}
