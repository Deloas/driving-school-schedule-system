package com.drivingschool.statistics.vo;

import lombok.Data;

/** 管理员总览统计 */
@Data
public class AdminOverviewVO {
    private long totalStudents;
    private long totalCoaches;
    private long totalVehicles;
    private long todayReservations;
    private long todayCompleted;
    private long todayAbsent;
    private long todaySchedules;
    private long todayAvailableSlots;
    private long adjustmentCount;
    private double adjustmentRate;
    private long completedTrainingCount;
}
