package com.drivingschool.statistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.drivingschool.coach.entity.Coach;
import com.drivingschool.coach.mapper.CoachMapper;
import com.drivingschool.reservation.entity.Reservation;
import com.drivingschool.reservation.mapper.ReservationMapper;
import com.drivingschool.schedule.entity.CoachSchedule;
import com.drivingschool.schedule.mapper.CoachScheduleMapper;
import com.drivingschool.statistics.service.StatisticsService;
import com.drivingschool.statistics.vo.*;
import com.drivingschool.student.entity.Student;
import com.drivingschool.student.mapper.StudentMapper;
import com.drivingschool.training.entity.TrainingRecord;
import com.drivingschool.training.mapper.TrainingRecordMapper;
import com.drivingschool.vehicle.mapper.VehicleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final StudentMapper studentMapper;
    private final CoachMapper coachMapper;
    private final VehicleMapper vehicleMapper;
    private final CoachScheduleMapper scheduleMapper;
    private final ReservationMapper reservationMapper;
    private final TrainingRecordMapper trainingRecordMapper;

    @Override
    public AdminOverviewVO adminOverview() {
        AdminOverviewVO vo = new AdminOverviewVO();
        LocalDate today = LocalDate.now();

        vo.setTotalStudents(studentMapper.selectCount(null));
        vo.setTotalCoaches(coachMapper.selectCount(new LambdaQueryWrapper<Coach>().eq(Coach::getStatus, "NORMAL")));
        vo.setTotalVehicles(vehicleMapper.selectCount(null));

        vo.setTodayReservations(reservationMapper.selectCount(new LambdaQueryWrapper<Reservation>().eq(Reservation::getReservationDate, today)));
        vo.setTodayCompleted(reservationMapper.selectCount(new LambdaQueryWrapper<Reservation>().eq(Reservation::getReservationDate, today).eq(Reservation::getStatus, "COMPLETED")));
        vo.setTodayAbsent(reservationMapper.selectCount(new LambdaQueryWrapper<Reservation>().eq(Reservation::getReservationDate, today).eq(Reservation::getStatus, "ABSENT")));

        long schedules = scheduleMapper.selectCount(new LambdaQueryWrapper<CoachSchedule>().eq(CoachSchedule::getScheduleDate, today));
        vo.setTodaySchedules(schedules);

        var todaySchedules = scheduleMapper.selectList(new LambdaQueryWrapper<CoachSchedule>().eq(CoachSchedule::getScheduleDate, today));
        long avail = todaySchedules.stream().mapToLong(s -> Math.max(0, s.getMaxStudents() - s.getCurrentStudents())).sum();
        vo.setTodayAvailableSlots(avail);

        long adj = reservationMapper.selectCount(new LambdaQueryWrapper<Reservation>().eq(Reservation::getIsAdjusted, 1));
        long total = reservationMapper.selectCount(null);
        vo.setAdjustmentCount(adj);
        vo.setAdjustmentRate(total > 0 ? Math.round(adj * 1000.0 / total) / 10.0 : 0);

        vo.setCompletedTrainingCount(trainingRecordMapper.selectCount(null));
        return vo;
    }

    @Override
    public List<CoachLoadVO> coachLoad() {
        return coachMapper.selectList(new LambdaQueryWrapper<Coach>().eq(Coach::getStatus, "NORMAL")).stream().map(c -> {
            CoachLoadVO v = new CoachLoadVO();
            v.setCoachId(c.getId()); v.setCoachName(c.getName());
            long resCount = reservationMapper.selectCount(new LambdaQueryWrapper<Reservation>().eq(Reservation::getActualCoachId, c.getId()));
            long compCount = reservationMapper.selectCount(new LambdaQueryWrapper<Reservation>().eq(Reservation::getActualCoachId, c.getId()).eq(Reservation::getStatus, "COMPLETED"));
            long schedCount = scheduleMapper.selectCount(new LambdaQueryWrapper<CoachSchedule>().eq(CoachSchedule::getCoachId, c.getId()));
            long cap = scheduleMapper.selectList(new LambdaQueryWrapper<CoachSchedule>().eq(CoachSchedule::getCoachId, c.getId())).stream().mapToLong(CoachSchedule::getMaxStudents).sum();
            long used = scheduleMapper.selectList(new LambdaQueryWrapper<CoachSchedule>().eq(CoachSchedule::getCoachId, c.getId())).stream().mapToLong(CoachSchedule::getCurrentStudents).sum();
            v.setReservationCount(resCount); v.setCompletedCount(compCount);
            v.setScheduleCount(schedCount); v.setCapacity(cap); v.setUsedCapacity(used);
            v.setUtilizationRate(cap > 0 ? Math.round(used * 1000.0 / cap) / 10.0 : 0);
            return v;
        }).sorted((a, b) -> Double.compare(b.getUtilizationRate(), a.getUtilizationRate())).collect(Collectors.toList());
    }

    @Override
    public List<ReservationTrendVO> reservationTrend() {
        List<ReservationTrendVO> list = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM-dd");
        for (int i = 6; i >= 0; i--) {
            LocalDate d = LocalDate.now().minusDays(i);
            ReservationTrendVO v = new ReservationTrendVO();
            v.setDate(d);
            v.setReservationCount(reservationMapper.selectCount(new LambdaQueryWrapper<Reservation>().eq(Reservation::getReservationDate, d)));
            v.setCompletedCount(reservationMapper.selectCount(new LambdaQueryWrapper<Reservation>().eq(Reservation::getReservationDate, d).eq(Reservation::getStatus, "COMPLETED")));
            v.setCancelledCount(reservationMapper.selectCount(new LambdaQueryWrapper<Reservation>().eq(Reservation::getReservationDate, d).eq(Reservation::getStatus, "CANCELLED")));
            v.setAbsentCount(reservationMapper.selectCount(new LambdaQueryWrapper<Reservation>().eq(Reservation::getReservationDate, d).eq(Reservation::getStatus, "ABSENT")));
            list.add(v);
        }
        return list;
    }

    @Override
    public List<ScheduleUtilizationVO> scheduleUtilization() {
        List<ScheduleUtilizationVO> list = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate d = LocalDate.now().minusDays(i);
            var schedules = scheduleMapper.selectList(new LambdaQueryWrapper<CoachSchedule>().eq(CoachSchedule::getScheduleDate, d));
            long cap = schedules.stream().mapToLong(CoachSchedule::getMaxStudents).sum();
            long used = schedules.stream().mapToLong(CoachSchedule::getCurrentStudents).sum();
            ScheduleUtilizationVO v = new ScheduleUtilizationVO();
            v.setDate(d); v.setTotalCapacity(cap); v.setUsedCapacity(used);
            v.setRemainCapacity(Math.max(0, cap - used));
            v.setUtilizationRate(cap > 0 ? Math.round(used * 1000.0 / cap) / 10.0 : 0);
            list.add(v);
        }
        return list;
    }

    @Override
    public List<StudentProgressVO> studentProgress() {
        List<Student> students = studentMapper.selectList(null);
        long r0=0, r1=0, r2=0, r3=0;
        for (Student s : students) {
            int c = s.getCompletedTrainingCount();
            if (c == 0) r0++; else if (c <= 3) r1++; else if (c <= 7) r2++; else r3++;
        }
        return List.of(
                buildProgress("0 次", r0),
                buildProgress("1-3 次", r1),
                buildProgress("4-7 次", r2),
                buildProgress("8 次及以上", r3));
    }

    @Override
    public List<RecentActivityVO> recentActivities() {
        List<RecentActivityVO> list = new ArrayList<>();
        DateTimeFormatter tf = DateTimeFormatter.ofPattern("MM-dd HH:mm");
        var reservations = reservationMapper.selectList(new LambdaQueryWrapper<Reservation>().orderByDesc(Reservation::getCreateTime).last("LIMIT 10"));
        for (Reservation r : reservations) {
            RecentActivityVO a = new RecentActivityVO();
            Student st = studentMapper.selectById(r.getStudentId());
            Coach ac = coachMapper.selectById(r.getActualCoachId());
            Coach mc = coachMapper.selectById(r.getMainCoachId());
            String sname = st != null ? st.getName() : "学员";
            String aname = ac != null ? ac.getName() : "教练";
            switch (r.getStatus()) {
                case "SUCCESS" -> { a.setType("RESERVATION"); a.setTitle(sname + " 预约了 " + aname); }
                case "COMPLETED" -> { a.setType("COMPLETED"); a.setTitle(sname + " 完成了 " + aname + " 的练车"); }
                case "ABSENT" -> { a.setType("ABSENT"); a.setTitle(sname + " 缺席了 " + aname + " 的练车"); }
                case "CANCELLED" -> { a.setType("CANCELLED"); a.setTitle(sname + " 取消了预约"); }
            }
            if (r.getIsAdjusted() == 1 && mc != null) a.setDescription("由 " + mc.getName() + " 调剂到 " + aname);
            a.setDescription((a.getDescription() != null ? a.getDescription() + " · " : "") + r.getReservationDate() + " " + ("MORNING".equals(r.getTimeSlot()) ? "上午" : "下午"));
            a.setTime(r.getCreateTime() != null ? r.getCreateTime().format(tf) : "");
            list.add(a);
        }
        return list;
    }

    @Override
    public CoachOverviewVO coachOverview(Long coachId) {
        CoachOverviewVO vo = new CoachOverviewVO();
        LocalDate today = LocalDate.now();
        vo.setTodayReservations(reservationMapper.selectCount(new LambdaQueryWrapper<Reservation>().eq(Reservation::getActualCoachId, coachId).eq(Reservation::getReservationDate, today)));
        vo.setTodayCompleted(reservationMapper.selectCount(new LambdaQueryWrapper<Reservation>().eq(Reservation::getActualCoachId, coachId).eq(Reservation::getReservationDate, today).eq(Reservation::getStatus, "COMPLETED")));
        vo.setTodayAbsent(reservationMapper.selectCount(new LambdaQueryWrapper<Reservation>().eq(Reservation::getActualCoachId, coachId).eq(Reservation::getReservationDate, today).eq(Reservation::getStatus, "ABSENT")));
        vo.setUpcomingReservations(reservationMapper.selectCount(new LambdaQueryWrapper<Reservation>().eq(Reservation::getActualCoachId, coachId).ge(Reservation::getReservationDate, today).eq(Reservation::getStatus, "SUCCESS")));
        vo.setCompletedTrainingCount(trainingRecordMapper.selectCount(new LambdaQueryWrapper<TrainingRecord>().eq(TrainingRecord::getCoachId, coachId)));
        vo.setAdjustmentReceivedCount(reservationMapper.selectCount(new LambdaQueryWrapper<Reservation>().eq(Reservation::getActualCoachId, coachId).eq(Reservation::getIsAdjusted, 1)));
        return vo;
    }

    @Override
    public StudentOverviewVO studentOverview(Long studentId) {
        StudentOverviewVO vo = new StudentOverviewVO();
        Student st = studentMapper.selectById(studentId);
        if (st == null) return vo;

        vo.setCompletedTrainingCount(st.getCompletedTrainingCount());
        vo.setSuccessReservationCount(reservationMapper.selectCount(new LambdaQueryWrapper<Reservation>().eq(Reservation::getStudentId, studentId).eq(Reservation::getStatus, "SUCCESS")));
        vo.setCancelledReservationCount(reservationMapper.selectCount(new LambdaQueryWrapper<Reservation>().eq(Reservation::getStudentId, studentId).eq(Reservation::getStatus, "CANCELLED")));
        vo.setAbsentCount(reservationMapper.selectCount(new LambdaQueryWrapper<Reservation>().eq(Reservation::getStudentId, studentId).eq(Reservation::getStatus, "ABSENT")));
        vo.setAdjustmentCount(reservationMapper.selectCount(new LambdaQueryWrapper<Reservation>().eq(Reservation::getStudentId, studentId).eq(Reservation::getIsAdjusted, 1)));

        var next = reservationMapper.selectList(new LambdaQueryWrapper<Reservation>().eq(Reservation::getStudentId, studentId).eq(Reservation::getStatus, "SUCCESS").ge(Reservation::getReservationDate, LocalDate.now()).orderByAsc(Reservation::getReservationDate).last("LIMIT 1"));
        if (!next.isEmpty()) {
            Reservation n = next.get(0);
            Coach c = coachMapper.selectById(n.getActualCoachId());
            vo.setNextReservation(n.getReservationDate() + " " + ("MORNING".equals(n.getTimeSlot()) ? "上午" : "下午") + " · " + (c != null ? c.getName() : ""));
        }

        var rec = trainingRecordMapper.selectList(new LambdaQueryWrapper<TrainingRecord>().eq(TrainingRecord::getStudentId, studentId).orderByDesc(TrainingRecord::getCreateTime).last("LIMIT 1"));
        if (!rec.isEmpty()) {
            TrainingRecord t = rec.get(0);
            Coach c = coachMapper.selectById(t.getCoachId());
            vo.setRecentTrainingRecord((t.getTrainingContent() != null ? t.getTrainingContent() : "练车") + " · " + (c != null ? c.getName() : ""));
        }
        return vo;
    }

    private StudentProgressVO buildProgress(String name, long count) {
        StudentProgressVO v = new StudentProgressVO();
        v.setRangeName(name); v.setStudentCount(count);
        return v;
    }
}
