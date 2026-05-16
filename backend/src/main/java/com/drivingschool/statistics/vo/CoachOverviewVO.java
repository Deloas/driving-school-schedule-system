package com.drivingschool.statistics.vo;

import lombok.Data;

/** 教练个人看板 */
@Data
public class CoachOverviewVO {
    private long todayReservations;
    private long todayCompleted;
    private long todayAbsent;
    private long upcomingReservations;
    private long completedTrainingCount;
    private long adjustmentReceivedCount;
}
