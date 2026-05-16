package com.drivingschool.training.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drivingschool.coach.entity.Coach;
import com.drivingschool.coach.mapper.CoachMapper;
import com.drivingschool.student.entity.Student;
import com.drivingschool.student.mapper.StudentMapper;
import com.drivingschool.training.dto.TrainingRecordQueryDTO;
import com.drivingschool.training.entity.TrainingRecord;
import com.drivingschool.training.mapper.TrainingRecordMapper;
import com.drivingschool.training.service.TrainingRecordService;
import com.drivingschool.training.vo.TrainingRecordVO;
import com.drivingschool.vehicle.entity.Vehicle;
import com.drivingschool.vehicle.mapper.VehicleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainingRecordServiceImpl implements TrainingRecordService {

    private final TrainingRecordMapper trainingRecordMapper;
    private final StudentMapper studentMapper;
    private final CoachMapper coachMapper;
    private final VehicleMapper vehicleMapper;

    @Override
    public Page<TrainingRecordVO> myRecords(Long studentId, Integer page, Integer size) {
        Page<TrainingRecord> p = trainingRecordMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<TrainingRecord>().eq(TrainingRecord::getStudentId, studentId).orderByDesc(TrainingRecord::getCreateTime));
        return toVOPage(p);
    }

    @Override
    public Page<TrainingRecordVO> coachRecords(Long coachId, Integer page, Integer size) {
        Page<TrainingRecord> p = trainingRecordMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<TrainingRecord>().eq(TrainingRecord::getCoachId, coachId).orderByDesc(TrainingRecord::getCreateTime));
        return toVOPage(p);
    }

    @Override
    public Page<TrainingRecordVO> adminQuery(TrainingRecordQueryDTO dto) {
        LambdaQueryWrapper<TrainingRecord> w = new LambdaQueryWrapper<>();
        if (dto.getCoachId() != null) w.eq(TrainingRecord::getCoachId, dto.getCoachId());
        if (dto.getStartDate() != null) w.ge(TrainingRecord::getTrainingDate, dto.getStartDate());
        if (dto.getEndDate() != null) w.le(TrainingRecord::getTrainingDate, dto.getEndDate());
        if (StringUtils.hasText(dto.getStudentName())) {
            var students = studentMapper.selectList(new LambdaQueryWrapper<Student>().like(Student::getName, dto.getStudentName()));
            if (!students.isEmpty()) w.in(TrainingRecord::getStudentId, students.stream().map(Student::getId).collect(Collectors.toList()));
            else w.eq(TrainingRecord::getStudentId, -1L);
        }
        w.orderByDesc(TrainingRecord::getCreateTime);
        Page<TrainingRecord> p = trainingRecordMapper.selectPage(new Page<>(dto.getPage(), dto.getSize()), w);
        return toVOPage(p);
    }

    private Page<TrainingRecordVO> toVOPage(Page<TrainingRecord> p) {
        Page<TrainingRecordVO> voPage = new Page<>(p.getCurrent(), p.getSize(), p.getTotal());
        voPage.setRecords(p.getRecords().stream().map(this::toVO).collect(Collectors.toList()));
        return voPage;
    }

    private TrainingRecordVO toVO(TrainingRecord r) {
        TrainingRecordVO vo = new TrainingRecordVO();
        vo.setId(r.getId()); vo.setReservationId(r.getReservationId());
        vo.setStudentId(r.getStudentId()); vo.setCoachId(r.getCoachId());
        vo.setVehicleId(r.getVehicleId()); vo.setTrainingDate(r.getTrainingDate());
        vo.setTimeSlot(r.getTimeSlot()); vo.setTrainingContent(r.getTrainingContent());
        vo.setResult(r.getResult()); vo.setCoachComment(r.getCoachComment());
        vo.setCreateTime(r.getCreateTime());
        Student s = studentMapper.selectById(r.getStudentId());
        if (s != null) vo.setStudentName(s.getName());
        Coach c = coachMapper.selectById(r.getCoachId());
        if (c != null) vo.setCoachName(c.getName());
        if (r.getVehicleId() != null) {
            Vehicle v = vehicleMapper.selectById(r.getVehicleId());
            if (v != null) vo.setPlateNumber(v.getPlateNumber());
        }
        return vo;
    }
}
