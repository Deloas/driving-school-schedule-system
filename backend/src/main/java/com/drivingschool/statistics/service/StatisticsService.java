package com.drivingschool.statistics.service;

import com.drivingschool.statistics.vo.*;
import java.util.List;

public interface StatisticsService {
    AdminOverviewVO adminOverview();
    List<CoachLoadVO> coachLoad();
    List<ReservationTrendVO> reservationTrend();
    List<ScheduleUtilizationVO> scheduleUtilization();
    List<StudentProgressVO> studentProgress();
    List<RecentActivityVO> recentActivities();
    CoachOverviewVO coachOverview(Long coachId);
    StudentOverviewVO studentOverview(Long studentId);
}
