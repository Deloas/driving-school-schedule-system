package com.drivingschool.statistics.vo;

import lombok.Data;
import java.time.LocalDate;

/** 排班利用率 */
@Data
public class ScheduleUtilizationVO {
    private LocalDate date;
    private long totalCapacity;
    private long usedCapacity;
    private long remainCapacity;
    private double utilizationRate;
}
