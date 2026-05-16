package com.drivingschool.statistics.vo;

import lombok.Data;

/** 教练负载 */
@Data
public class CoachLoadVO {
    private Long coachId;
    private String coachName;
    private long scheduleCount;
    private long reservationCount;
    private long completedCount;
    private long capacity;
    private long usedCapacity;
    private double utilizationRate;
}
