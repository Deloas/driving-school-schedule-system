package com.drivingschool.reservation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drivingschool.coach.entity.Coach;
import com.drivingschool.coach.mapper.CoachMapper;
import com.drivingschool.common.exception.BusinessException;
import com.drivingschool.reservation.dto.ReservationCancelDTO;
import com.drivingschool.reservation.dto.ReservationCreateDTO;
import com.drivingschool.reservation.dto.ReservationQueryDTO;
import com.drivingschool.reservation.entity.Reservation;
import com.drivingschool.reservation.mapper.ReservationMapper;
import com.drivingschool.reservation.service.ReservationService;
import com.drivingschool.reservation.vo.ReservationOptionVO;
import com.drivingschool.reservation.vo.ReservationVO;
import com.drivingschool.schedule.entity.CoachSchedule;
import com.drivingschool.schedule.mapper.CoachScheduleMapper;
import com.drivingschool.student.entity.Student;
import com.drivingschool.student.mapper.StudentMapper;
import com.drivingschool.vehicle.entity.Vehicle;
import com.drivingschool.vehicle.mapper.VehicleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * 预约业务实现
 * <p>
 * M5 阶段只允许学员预约自己的主教练，不做调剂。
 * 创建和取消预约均使用事务，并用条件 UPDATE 兜底防止超额。
 * </p>
 */
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationMapper reservationMapper;
    private final CoachScheduleMapper scheduleMapper;
    private final StudentMapper studentMapper;
    private final CoachMapper coachMapper;
    private final VehicleMapper vehicleMapper;

    @Override
    public ReservationOptionVO getOptions(Long studentId, LocalDate date, String timeSlot) {
        Student student = studentMapper.selectById(studentId);
        if (student == null) throw new BusinessException("学员信息不存在");

        ReservationOptionVO vo = new ReservationOptionVO();
        ReservationOptionVO.MainCoachOption opt = new ReservationOptionVO.MainCoachOption();

        // 查询主教练在该日期时间段的排班
        CoachSchedule schedule = scheduleMapper.selectOne(new LambdaQueryWrapper<CoachSchedule>()
                .eq(CoachSchedule::getCoachId, student.getMainCoachId())
                .eq(CoachSchedule::getScheduleDate, date)
                .eq(CoachSchedule::getTimeSlot, timeSlot));

        if (schedule == null) {
            opt.setAvailable(false);
            opt.setMessage("当前日期和时间段暂无排班，请选择其他时间");
        } else if (!"OPEN".equals(schedule.getStatus())) {
            opt.setAvailable(false);
            opt.setMessage("该时间段排班已关闭");
        } else if (schedule.getCurrentStudents() >= schedule.getMaxStudents()) {
            opt.setCoachId(schedule.getCoachId());
            opt.setScheduleId(schedule.getId());
            opt.setCurrentStudents(schedule.getCurrentStudents());
            opt.setMaxStudents(schedule.getMaxStudents());
            opt.setRemainCount(0);
            opt.setAvailable(false);
            opt.setMessage("主教练当前时间段已满员，请选择其他时间段");
            fillCoachAndVehicle(opt, schedule);
        } else {
            opt.setScheduleId(schedule.getId());
            opt.setCoachId(schedule.getCoachId());
            opt.setCurrentStudents(schedule.getCurrentStudents());
            opt.setMaxStudents(schedule.getMaxStudents());
            opt.setRemainCount(schedule.getMaxStudents() - schedule.getCurrentStudents());
            opt.setScheduleDate(schedule.getScheduleDate().toString());
            opt.setTimeSlot(schedule.getTimeSlot());
            opt.setAvailable(true);
            opt.setMessage("可预约");
            fillCoachAndVehicle(opt, schedule);
        }

        vo.setMainCoachOption(opt);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReservationVO create(ReservationCreateDTO dto, Long studentId) {
        Student student = studentMapper.selectById(studentId);
        if (student == null) throw new BusinessException("学员信息不存在");

        // 查询排班
        CoachSchedule schedule = scheduleMapper.selectById(dto.getScheduleId());
        if (schedule == null) throw new BusinessException("排班不存在");
        if (!"OPEN".equals(schedule.getStatus())) throw new BusinessException("该排班未开放预约");

        // 校验预约主教练（M5 只允许主教练）
        if (!schedule.getCoachId().equals(student.getMainCoachId())) {
            throw new BusinessException("当前阶段只能预约自己的主教练");
        }

        // 校验同一日期同一时间段不重复预约
        Long dup = reservationMapper.selectCount(new LambdaQueryWrapper<Reservation>()
                .eq(Reservation::getStudentId, studentId)
                .eq(Reservation::getReservationDate, schedule.getScheduleDate())
                .eq(Reservation::getTimeSlot, schedule.getTimeSlot())
                .eq(Reservation::getStatus, "SUCCESS"));
        if (dup > 0) throw new BusinessException("你已预约该时间段，不能重复预约");

        // 条件更新 current_students（数据库层面防超额）
        int updated = reservationMapper.increaseCurrentStudents(dto.getScheduleId());
        if (updated == 0) throw new BusinessException("该时间段已满员或不可预约");

        // 创建预约记录
        Reservation r = new Reservation();
        r.setStudentId(studentId);
        r.setMainCoachId(student.getMainCoachId());
        r.setActualCoachId(schedule.getCoachId());
        r.setVehicleId(schedule.getVehicleId());
        r.setScheduleId(schedule.getId());
        r.setReservationDate(schedule.getScheduleDate());
        r.setTimeSlot(schedule.getTimeSlot());
        r.setStatus("SUCCESS");
        r.setIsAdjusted(0);
        r.setCreateTime(LocalDateTime.now());
        reservationMapper.insert(r);

        return toVO(r);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long reservationId, ReservationCancelDTO dto, Long operatorId, String operatorRole) {
        Reservation r = reservationMapper.selectById(reservationId);
        if (r == null) throw new BusinessException("预约不存在");
        if (!"SUCCESS".equals(r.getStatus())) throw new BusinessException("该预约不能取消");

        // 学员只能取消自己的，管理员可取消任意
        if ("STUDENT".equals(operatorRole) && !r.getStudentId().equals(operatorId)) {
            throw new BusinessException("只能取消自己的预约");
        }

        // 更新预约状态
        r.setStatus("CANCELLED");
        r.setCancelTime(LocalDateTime.now());
        if (StringUtils.hasText(dto.getReason())) r.setCancelReason(dto.getReason());
        reservationMapper.updateById(r);

        // 减少排班当前人数
        reservationMapper.decreaseCurrentStudents(r.getScheduleId());
    }

    @Override
    public Page<ReservationVO> myReservations(Long studentId, Integer page, Integer size) {
        Page<Reservation> p = reservationMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Reservation>()
                        .eq(Reservation::getStudentId, studentId)
                        .orderByDesc(Reservation::getCreateTime));
        Page<ReservationVO> voPage = new Page<>(p.getCurrent(), p.getSize(), p.getTotal());
        voPage.setRecords(p.getRecords().stream().map(this::toVO).collect(Collectors.toList()));
        return voPage;
    }

    @Override
    public Page<ReservationVO> coachToday(Long coachId, LocalDate date, Integer page, Integer size) {
        if (date == null) date = LocalDate.now();
        Page<Reservation> p = reservationMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Reservation>()
                        .eq(Reservation::getActualCoachId, coachId)
                        .eq(Reservation::getReservationDate, date)
                        .orderByAsc(Reservation::getTimeSlot));
        Page<ReservationVO> voPage = new Page<>(p.getCurrent(), p.getSize(), p.getTotal());
        voPage.setRecords(p.getRecords().stream().map(this::toVO).collect(Collectors.toList()));
        return voPage;
    }

    @Override
    public Page<ReservationVO> adminQuery(ReservationQueryDTO dto) {
        LambdaQueryWrapper<Reservation> w = new LambdaQueryWrapper<>();
        if (dto.getCoachId() != null) w.eq(Reservation::getActualCoachId, dto.getCoachId());
        if (dto.getReservationDate() != null) w.eq(Reservation::getReservationDate, dto.getReservationDate());
        if (StringUtils.hasText(dto.getTimeSlot())) w.eq(Reservation::getTimeSlot, dto.getTimeSlot());
        if (StringUtils.hasText(dto.getStatus())) w.eq(Reservation::getStatus, dto.getStatus());
        // studentName 模糊搜索：先查 studentId，再过滤
        if (StringUtils.hasText(dto.getStudentName())) {
            // 简化处理：通过 student 表 name like 查询得到 studentIds
            var students = studentMapper.selectList(new LambdaQueryWrapper<Student>()
                    .like(Student::getName, dto.getStudentName()));
            if (!students.isEmpty()) {
                w.in(Reservation::getStudentId, students.stream().map(Student::getId).collect(Collectors.toList()));
            } else {
                w.eq(Reservation::getStudentId, -1L); // 无匹配
            }
        }
        w.orderByDesc(Reservation::getCreateTime);

        Page<Reservation> p = reservationMapper.selectPage(new Page<>(dto.getPage(), dto.getSize()), w);
        Page<ReservationVO> voPage = new Page<>(p.getCurrent(), p.getSize(), p.getTotal());
        voPage.setRecords(p.getRecords().stream().map(this::toVO).collect(Collectors.toList()));
        return voPage;
    }

    // ==================== 内部方法 ====================

    private void fillCoachAndVehicle(ReservationOptionVO.MainCoachOption opt, CoachSchedule s) {
        Coach c = coachMapper.selectById(s.getCoachId());
        opt.setCoachName(c != null ? c.getName() : "未知");
        if (s.getVehicleId() != null) {
            Vehicle v = vehicleMapper.selectById(s.getVehicleId());
            opt.setVehicleId(s.getVehicleId());
            opt.setPlateNumber(v != null ? v.getPlateNumber() : null);
        }
    }

    private ReservationVO toVO(Reservation r) {
        ReservationVO vo = new ReservationVO();
        vo.setId(r.getId());
        vo.setStudentId(r.getStudentId());
        vo.setMainCoachId(r.getMainCoachId());
        vo.setActualCoachId(r.getActualCoachId());
        vo.setVehicleId(r.getVehicleId());
        vo.setScheduleId(r.getScheduleId());
        vo.setReservationDate(r.getReservationDate());
        vo.setTimeSlot(r.getTimeSlot());
        vo.setStatus(r.getStatus());
        vo.setIsAdjusted(r.getIsAdjusted());
        vo.setAdjustReason(r.getAdjustReason());
        vo.setCancelReason(r.getCancelReason());
        vo.setCreateTime(r.getCreateTime());
        vo.setCancelTime(r.getCancelTime());

        // 填充姓名
        Student st = studentMapper.selectById(r.getStudentId());
        if (st != null) { vo.setStudentName(st.getName()); vo.setStudentPhone(st.getPhone()); }
        Coach mc = coachMapper.selectById(r.getMainCoachId());
        if (mc != null) vo.setMainCoachName(mc.getName());
        Coach ac = coachMapper.selectById(r.getActualCoachId());
        if (ac != null) vo.setActualCoachName(ac.getName());
        if (r.getVehicleId() != null) {
            Vehicle v = vehicleMapper.selectById(r.getVehicleId());
            if (v != null) vo.setPlateNumber(v.getPlateNumber());
        }
        return vo;
    }
}
