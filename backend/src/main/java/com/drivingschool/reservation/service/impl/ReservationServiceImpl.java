package com.drivingschool.reservation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drivingschool.coach.entity.Coach;
import com.drivingschool.coach.mapper.CoachMapper;
import com.drivingschool.common.constant.RedisKeyConstants;
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
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 预约业务实现
 * <p>
 * M6 阶段：加入 Redisson 分布式锁（锁粒度 scheduleId），确保并发预约不会超额。
 * 锁内重新校验所有条件，并以数据库条件 UPDATE 作为最终兜底。
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationMapper reservationMapper;
    private final CoachScheduleMapper scheduleMapper;
    private final StudentMapper studentMapper;
    private final CoachMapper coachMapper;
    private final VehicleMapper vehicleMapper;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public ReservationOptionVO getOptions(Long studentId, LocalDate date, String timeSlot) {
        Student student = studentMapper.selectById(studentId);
        if (student == null) throw new BusinessException("学员信息不存在");

        ReservationOptionVO vo = new ReservationOptionVO();
        ReservationOptionVO.MainCoachOption opt = new ReservationOptionVO.MainCoachOption();

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
            fillMinimalOption(opt, schedule);
            opt.setAvailable(false);
            opt.setMessage("主教练当前时间段已满员，请选择其他时间段");
        } else {
            fillFullOption(opt, schedule);
            opt.setAvailable(true);
            opt.setMessage("可预约");
        }

        vo.setMainCoachOption(opt);
        return vo;
    }

    // ==================== 创建预约（M6：加入分布式锁 + 限流） ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReservationVO create(ReservationCreateDTO dto, Long studentId) {
        Long scheduleId = dto.getScheduleId();

        // --- 1. 防重复点击：学员级限流（轻量） ---
        String limitKey = RedisKeyConstants.LIMIT_RESERVATION + studentId;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(limitKey))) {
            throw new BusinessException("请勿频繁提交预约，请稍后再试");
        }
        stringRedisTemplate.opsForValue().set(limitKey, "1", Duration.ofSeconds(5));

        // --- 2. 加锁前基础校验 ---
        Student student = studentMapper.selectById(studentId);
        if (student == null) throw new BusinessException("学员信息不存在");

        // --- 3. Redisson 分布式锁 ---
        String lockKey = RedisKeyConstants.LOCK_SCHEDULE + scheduleId;
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked = false;

        try {
            // 等待 3 秒获取锁，锁自动释放时间 10 秒
            locked = lock.tryLock(3, 10, TimeUnit.SECONDS);
            if (!locked) {
                throw new BusinessException("当前预约人数较多，请稍后重试");
            }

            // --- 4. 锁内重新查询排班（避免使用加锁前可能过期的数据） ---
            CoachSchedule schedule = scheduleMapper.selectById(scheduleId);
            if (schedule == null) throw new BusinessException("排班不存在");
            if (!"OPEN".equals(schedule.getStatus())) throw new BusinessException("该排班未开放预约");

            // 锁内校验容量
            if (schedule.getCurrentStudents() >= schedule.getMaxStudents()) {
                throw new BusinessException("该时间段已满员或不可预约");
            }

            // 锁内校验主教练
            if (!schedule.getCoachId().equals(student.getMainCoachId())) {
                throw new BusinessException("当前阶段只能预约自己的主教练");
            }

            // 锁内校验重复预约
            Long dup = reservationMapper.selectCount(new LambdaQueryWrapper<Reservation>()
                    .eq(Reservation::getStudentId, studentId)
                    .eq(Reservation::getReservationDate, schedule.getScheduleDate())
                    .eq(Reservation::getTimeSlot, schedule.getTimeSlot())
                    .eq(Reservation::getStatus, "SUCCESS"));
            if (dup > 0) throw new BusinessException("你已预约该时间段，不能重复预约");

            // --- 5. 数据库条件 UPDATE 作为最终兜底 ---
            int updated = reservationMapper.increaseCurrentStudents(scheduleId);
            if (updated == 0) throw new BusinessException("该时间段已满员或不可预约");

            // --- 6. 创建预约记录 ---
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
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("预约处理中断，请稍后重试");
        } finally {
            // 锁必须释放，且只释放当前线程持有的锁
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    // ==================== 取消预约（M6：加锁保护 current_students 减操作） ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long reservationId, ReservationCancelDTO dto, Long operatorId, String operatorRole) {
        // 查询预约
        Reservation r = reservationMapper.selectById(reservationId);
        if (r == null) throw new BusinessException("预约不存在");
        if (!"SUCCESS".equals(r.getStatus())) throw new BusinessException("该预约不能取消");

        if ("STUDENT".equals(operatorRole) && !r.getStudentId().equals(operatorId)) {
            throw new BusinessException("只能取消自己的预约");
        }

        // 加排班锁，防止并发取消失败
        String lockKey = RedisKeyConstants.LOCK_SCHEDULE + r.getScheduleId();
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked = false;

        try {
            locked = lock.tryLock(3, 10, TimeUnit.SECONDS);
            if (!locked) {
                throw new BusinessException("系统繁忙，请稍后重试");
            }

            // 锁内更新状态
            r.setStatus("CANCELLED");
            r.setCancelTime(LocalDateTime.now());
            if (StringUtils.hasText(dto.getReason())) {
                r.setCancelReason(dto.getReason());
            }
            reservationMapper.updateById(r);

            // 条件减少当前人数（不会减到负数）
            reservationMapper.decreaseCurrentStudents(r.getScheduleId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("取消处理中断，请稍后重试");
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    // ==================== 查询方法（不变） ====================

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
        if (StringUtils.hasText(dto.getStudentName())) {
            var students = studentMapper.selectList(new LambdaQueryWrapper<Student>()
                    .like(Student::getName, dto.getStudentName()));
            if (!students.isEmpty()) {
                w.in(Reservation::getStudentId, students.stream().map(Student::getId).collect(Collectors.toList()));
            } else {
                w.eq(Reservation::getStudentId, -1L);
            }
        }
        w.orderByDesc(Reservation::getCreateTime);
        Page<Reservation> p = reservationMapper.selectPage(new Page<>(dto.getPage(), dto.getSize()), w);
        Page<ReservationVO> voPage = new Page<>(p.getCurrent(), p.getSize(), p.getTotal());
        voPage.setRecords(p.getRecords().stream().map(this::toVO).collect(Collectors.toList()));
        return voPage;
    }

    // ==================== 内部方法 ====================

    private void fillMinimalOption(ReservationOptionVO.MainCoachOption opt, CoachSchedule s) {
        opt.setScheduleId(s.getId());
        opt.setCoachId(s.getCoachId());
        opt.setCurrentStudents(s.getCurrentStudents());
        opt.setMaxStudents(s.getMaxStudents());
        opt.setRemainCount(0);
        fillCoachAndVehicle(opt, s);
    }

    private void fillFullOption(ReservationOptionVO.MainCoachOption opt, CoachSchedule s) {
        opt.setScheduleId(s.getId());
        opt.setCoachId(s.getCoachId());
        opt.setCurrentStudents(s.getCurrentStudents());
        opt.setMaxStudents(s.getMaxStudents());
        opt.setRemainCount(s.getMaxStudents() - s.getCurrentStudents());
        opt.setScheduleDate(s.getScheduleDate().toString());
        opt.setTimeSlot(s.getTimeSlot());
        fillCoachAndVehicle(opt, s);
    }

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
