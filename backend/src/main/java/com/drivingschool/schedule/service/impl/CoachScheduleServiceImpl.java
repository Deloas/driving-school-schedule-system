package com.drivingschool.schedule.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drivingschool.coach.entity.Coach;
import com.drivingschool.coach.mapper.CoachMapper;
import com.drivingschool.common.exception.BusinessException;
import com.drivingschool.schedule.dto.ScheduleBatchCreateDTO;
import com.drivingschool.schedule.dto.ScheduleCreateDTO;
import com.drivingschool.schedule.dto.ScheduleQueryDTO;
import com.drivingschool.schedule.dto.ScheduleUpdateDTO;
import com.drivingschool.schedule.entity.CoachSchedule;
import com.drivingschool.schedule.mapper.CoachScheduleMapper;
import com.drivingschool.schedule.service.CoachScheduleService;
import com.drivingschool.schedule.vo.ScheduleAvailableVO;
import com.drivingschool.schedule.vo.ScheduleVO;
import com.drivingschool.vehicle.entity.Vehicle;
import com.drivingschool.vehicle.mapper.VehicleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 排班业务实现
 */
@Service
@RequiredArgsConstructor
public class CoachScheduleServiceImpl implements CoachScheduleService {

    private final CoachScheduleMapper scheduleMapper;
    private final CoachMapper coachMapper;
    private final VehicleMapper vehicleMapper;

    @Override
    public Page<ScheduleVO> queryPage(ScheduleQueryDTO dto) {
        LambdaQueryWrapper<CoachSchedule> w = new LambdaQueryWrapper<>();
        if (dto.getCoachId() != null) w.eq(CoachSchedule::getCoachId, dto.getCoachId());
        if (dto.getVehicleId() != null) w.eq(CoachSchedule::getVehicleId, dto.getVehicleId());
        if (dto.getScheduleDate() != null) w.eq(CoachSchedule::getScheduleDate, dto.getScheduleDate());
        if (dto.getStartDate() != null) w.ge(CoachSchedule::getScheduleDate, dto.getStartDate());
        if (dto.getEndDate() != null) w.le(CoachSchedule::getScheduleDate, dto.getEndDate());
        if (StringUtils.hasText(dto.getTimeSlot())) w.eq(CoachSchedule::getTimeSlot, dto.getTimeSlot());
        if (StringUtils.hasText(dto.getStatus())) w.eq(CoachSchedule::getStatus, dto.getStatus());
        w.orderByDesc(CoachSchedule::getScheduleDate).orderByAsc(CoachSchedule::getTimeSlot);

        Page<CoachSchedule> page = scheduleMapper.selectPage(new Page<>(dto.getPage(), dto.getSize()), w);
        Page<ScheduleVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(page.getRecords().stream().map(this::toVO).collect(Collectors.toList()));
        return voPage;
    }

    @Override
    public ScheduleVO getById(Long id) {
        return toVO(findSchedule(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ScheduleVO create(ScheduleCreateDTO dto) {
        // 1. 校验教练
        Coach coach = coachMapper.selectById(dto.getCoachId());
        if (coach == null) throw new BusinessException("教练不存在");
        if (!"NORMAL".equals(coach.getStatus())) throw new BusinessException("教练已停用，不能创建排班");

        // 2. 校验时间段合法性
        if (!List.of("MORNING", "AFTERNOON").contains(dto.getTimeSlot())) {
            throw new BusinessException("无效的时间段，仅支持 MORNING / AFTERNOON");
        }

        // 3. 先校验教练重复，错误提示更符合业务语义
        checkDuplicate(dto.getCoachId(), dto.getScheduleDate(), dto.getTimeSlot(), null);

        // 4. 再校验车辆冲突
        if (dto.getVehicleId() != null) {
            validateVehicle(dto.getVehicleId());
            checkVehicleConflict(dto.getVehicleId(), dto.getScheduleDate(), dto.getTimeSlot(), null);
        }

        CoachSchedule schedule = new CoachSchedule();
        BeanUtils.copyProperties(dto, schedule);
        schedule.setCurrentStudents(0);
        schedule.setStatus("OPEN");
        scheduleMapper.insert(schedule);
        return toVO(schedule);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ScheduleVO update(Long id, ScheduleUpdateDTO dto) {
        CoachSchedule s = findSchedule(id);

        if (dto.getVehicleId() != null) {
            validateVehicle(dto.getVehicleId());
            checkVehicleConflict(dto.getVehicleId(), s.getScheduleDate(), s.getTimeSlot(), id);
            s.setVehicleId(dto.getVehicleId());
        }

        // maxStudents 不能小于 currentStudents
        if (dto.getMaxStudents() != null) {
            if (dto.getMaxStudents() < s.getCurrentStudents()) {
                throw new BusinessException("当前已有 " + s.getCurrentStudents() + " 人预约，不能将容量调小到 " + dto.getMaxStudents());
            }
            s.setMaxStudents(dto.getMaxStudents());
        }

        if (dto.getRemark() != null) {
            s.setRemark(dto.getRemark());
        }

        scheduleMapper.updateById(s);
        return toVO(s);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, String status) {
        if (!List.of("OPEN", "CLOSED", "CANCELLED").contains(status)) {
            throw new BusinessException("无效的排班状态：" + status);
        }
        CoachSchedule s = findSchedule(id);
        if ("CANCELLED".equals(status) && s.getCurrentStudents() > 0) {
            throw new BusinessException("当前已有 " + s.getCurrentStudents() + " 人预约，不能取消排班。请先关闭排班或处理已有预约");
        }
        s.setStatus(status);
        scheduleMapper.updateById(s);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Integer> batchCreate(ScheduleBatchCreateDTO dto) {
        int created = 0, skipped = 0;

        for (Long coachId : dto.getCoachIds()) {
            Coach coach = coachMapper.selectById(coachId);
            if (coach == null || !"NORMAL".equals(coach.getStatus())) { skipped++; continue; }

            for (LocalDate date = dto.getStartDate(); !date.isAfter(dto.getEndDate()); date = date.plusDays(1)) {
                for (String slot : dto.getTimeSlots()) {
                    // 校验是否已存在
                    Long count = scheduleMapper.selectCount(new LambdaQueryWrapper<CoachSchedule>()
                            .eq(CoachSchedule::getCoachId, coachId)
                            .eq(CoachSchedule::getScheduleDate, date)
                            .eq(CoachSchedule::getTimeSlot, slot));
                    if (count > 0) { skipped++; continue; }

                    CoachSchedule s = new CoachSchedule();
                    s.setCoachId(coachId);
                    s.setScheduleDate(date);
                    s.setTimeSlot(slot);
                    s.setMaxStudents(dto.getMaxStudents());
                    s.setCurrentStudents(0);
                    s.setStatus("OPEN");
                    s.setRemark(dto.getRemark());

                    // 自动绑定车辆
                    if (Boolean.TRUE.equals(dto.getAutoBindVehicle())) {
                        Vehicle v = vehicleMapper.selectOne(new LambdaQueryWrapper<Vehicle>()
                                .eq(Vehicle::getCoachId, coachId)
                                .eq(Vehicle::getStatus, "NORMAL")
                                .last("LIMIT 1"));
                        if (v != null) s.setVehicleId(v.getId());
                    }

                    scheduleMapper.insert(s);
                    created++;
                }
            }
        }

        Map<String, Integer> result = new HashMap<>();
        result.put("created", created);
        result.put("skipped", skipped);
        return result;
    }

    @Override
    public List<ScheduleAvailableVO> availableList(LocalDate date, String timeSlot) {
        return scheduleMapper.selectList(new LambdaQueryWrapper<CoachSchedule>()
                        .eq(CoachSchedule::getScheduleDate, date)
                        .eq(CoachSchedule::getTimeSlot, timeSlot)
                        .eq(CoachSchedule::getStatus, "OPEN")
                        .apply("current_students < max_students"))
                .stream()
                .map(s -> {
                    ScheduleAvailableVO vo = new ScheduleAvailableVO();
                    BeanUtils.copyProperties(s, vo);
                    vo.setScheduleId(s.getId());
                    vo.setRemainCount(s.getMaxStudents() - s.getCurrentStudents());
                    // 填充教练名和车牌
                    Coach c = coachMapper.selectById(s.getCoachId());
                    vo.setCoachName(c != null ? c.getName() : "未知");
                    if (s.getVehicleId() != null) {
                        Vehicle v = vehicleMapper.selectById(s.getVehicleId());
                        vo.setPlateNumber(v != null ? v.getPlateNumber() : null);
                    }
                    return vo;
                })
                .collect(Collectors.toList());
    }

    // ==================== 内部方法 ====================

    private CoachSchedule findSchedule(Long id) {
        CoachSchedule s = scheduleMapper.selectById(id);
        if (s == null) throw new BusinessException("排班不存在");
        return s;
    }

    private void validateVehicle(Long vehicleId) {
        Vehicle v = vehicleMapper.selectById(vehicleId);
        if (v == null) throw new BusinessException("车辆不存在");
        if (!"NORMAL".equals(v.getStatus())) throw new BusinessException("车辆当前不可用，不能绑定排班");
    }

    /** 检查教练是否在同时段已有排班 */
    private void checkDuplicate(Long coachId, LocalDate date, String slot, Long excludeId) {
        LambdaQueryWrapper<CoachSchedule> w = new LambdaQueryWrapper<CoachSchedule>()
                .eq(CoachSchedule::getCoachId, coachId)
                .eq(CoachSchedule::getScheduleDate, date)
                .eq(CoachSchedule::getTimeSlot, slot);
        if (excludeId != null) w.ne(CoachSchedule::getId, excludeId);
        if (scheduleMapper.selectCount(w) > 0) {
            throw new BusinessException("该教练当前时间段已有排班");
        }
    }

    /** 检查车辆是否在同时段已被其他排班占用 */
    private void checkVehicleConflict(Long vehicleId, LocalDate date, String slot, Long excludeId) {
        LambdaQueryWrapper<CoachSchedule> w = new LambdaQueryWrapper<CoachSchedule>()
                .eq(CoachSchedule::getVehicleId, vehicleId)
                .eq(CoachSchedule::getScheduleDate, date)
                .eq(CoachSchedule::getTimeSlot, slot);
        if (excludeId != null) w.ne(CoachSchedule::getId, excludeId);
        if (scheduleMapper.selectCount(w) > 0) {
            throw new BusinessException("该车辆当前时间段已被占用");
        }
    }

    private ScheduleVO toVO(CoachSchedule s) {
        ScheduleVO vo = new ScheduleVO();
        BeanUtils.copyProperties(s, vo);
        vo.setRemainCount(s.getMaxStudents() - s.getCurrentStudents());
        Coach c = coachMapper.selectById(s.getCoachId());
        vo.setCoachName(c != null ? c.getName() : "未知");
        if (s.getVehicleId() != null) {
            Vehicle v = vehicleMapper.selectById(s.getVehicleId());
            vo.setPlateNumber(v != null ? v.getPlateNumber() : null);
        }
        return vo;
    }
}
