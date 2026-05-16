package com.drivingschool.statistics.vo;

import lombok.Data;

/** 学员个人看板 */
@Data
public class StudentOverviewVO {
    private long completedTrainingCount;
    private long successReservationCount;
    private long cancelledReservationCount;
    private long absentCount;
    private long adjustmentCount;
    /** 下一次预约摘要 */
    private String nextReservation;
    /** 最近练车记录摘要 */
    private String recentTrainingRecord;
}
