package com.drivingschool.statistics.vo;

import lombok.Data;
import java.time.LocalDate;

/** 预约趋势 */
@Data
public class ReservationTrendVO {
    private LocalDate date;
    private long reservationCount;
    private long completedCount;
    private long cancelledCount;
    private long absentCount;
}
