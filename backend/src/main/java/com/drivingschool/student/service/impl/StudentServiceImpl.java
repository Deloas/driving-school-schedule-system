package com.drivingschool.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drivingschool.coach.entity.Coach;
import com.drivingschool.coach.mapper.CoachMapper;
import com.drivingschool.common.exception.BusinessException;
import com.drivingschool.student.dto.StudentCreateDTO;
import com.drivingschool.student.dto.StudentQueryDTO;
import com.drivingschool.student.dto.StudentUpdateDTO;
import com.drivingschool.student.entity.Student;
import com.drivingschool.student.mapper.StudentMapper;
import com.drivingschool.student.service.StudentService;
import com.drivingschool.student.vo.StudentSimpleVO;
import com.drivingschool.student.vo.StudentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 学员业务实现
 * <p>
 * 新增学员时必须校验主教练是否存在且为正常状态，
 * 修改学员时如果变更了 completed_training_count 必须 <= required_training_count。
 * </p>
 */
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentMapper studentMapper;
    private final CoachMapper coachMapper;

    @Override
    public Page<StudentVO> queryPage(StudentQueryDTO dto) {
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(dto.getKeyword())) {
            wrapper.and(w -> w
                    .like(Student::getName, dto.getKeyword())
                    .or()
                    .like(Student::getPhone, dto.getKeyword()));
        }
        if (dto.getCoachId() != null) {
            wrapper.eq(Student::getMainCoachId, dto.getCoachId());
        }
        if (StringUtils.hasText(dto.getStatus())) {
            wrapper.eq(Student::getStatus, dto.getStatus());
        }
        wrapper.orderByDesc(Student::getCreateTime);

        Page<Student> page = studentMapper.selectPage(
                new Page<>(dto.getPage(), dto.getSize()), wrapper);

        Page<StudentVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(page.getRecords().stream().map(this::toVO).collect(Collectors.toList()));
        return voPage;
    }

    @Override
    public StudentVO getById(Long id) {
        return toVO(findStudent(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudentVO create(StudentCreateDTO dto) {
        // 校验主教练是否存在且状态正常
        Coach coach = coachMapper.selectById(dto.getMainCoachId());
        if (coach == null) {
            throw new BusinessException("所选教练不存在");
        }
        if (!"NORMAL".equals(coach.getStatus())) {
            throw new BusinessException("所选教练当前不在职，无法分配学员");
        }

        Student student = new Student();
        BeanUtils.copyProperties(dto, student);
        student.setCompletedTrainingCount(0);
        student.setStatus("NORMAL");
        studentMapper.insert(student);

        return toVO(student);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudentVO update(Long id, StudentUpdateDTO dto) {
        Student student = findStudent(id);

        // 如果更换了主教练，校验新教练是否存在且状态正常
        if (dto.getMainCoachId() != null && !dto.getMainCoachId().equals(student.getMainCoachId())) {
            Coach coach = coachMapper.selectById(dto.getMainCoachId());
            if (coach == null) {
                throw new BusinessException("所选教练不存在");
            }
            if (!"NORMAL".equals(coach.getStatus())) {
                throw new BusinessException("所选教练当前不在职，无法分配学员");
            }
        }

        // 校验应完成次数 >= 已完成次数
        if (dto.getRequiredTrainingCount() != null
                && dto.getRequiredTrainingCount() < student.getCompletedTrainingCount()) {
            throw new BusinessException("应完成练车次数不能小于已完成次数("
                    + student.getCompletedTrainingCount() + ")");
        }

        BeanUtils.copyProperties(dto, student, "id", "completedTrainingCount", "status", "createTime", "updateTime");
        studentMapper.updateById(student);

        return toVO(student);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, String status) {
        if (!List.of("NORMAL", "STOPPED").contains(status)) {
            throw new BusinessException("无效的学员状态：" + status);
        }
        Student student = findStudent(id);
        student.setStatus(status);
        studentMapper.updateById(student);
    }

    @Override
    public List<StudentSimpleVO> simpleList() {
        return studentMapper.selectList(
                        new LambdaQueryWrapper<Student>()
                                .eq(Student::getStatus, "NORMAL")
                                .orderByAsc(Student::getName))
                .stream()
                .map(s -> {
                    StudentSimpleVO vo = new StudentSimpleVO();
                    vo.setId(s.getId());
                    vo.setName(s.getName());
                    vo.setPhone(s.getPhone());
                    vo.setStatus(s.getStatus());
                    return vo;
                })
                .collect(Collectors.toList());
    }

    private Student findStudent(Long id) {
        Student student = studentMapper.selectById(id);
        if (student == null) {
            throw new BusinessException("学员不存在");
        }
        return student;
    }

    /** 转换为 VO，并填充主教练姓名 */
    private StudentVO toVO(Student student) {
        StudentVO vo = new StudentVO();
        BeanUtils.copyProperties(student, vo);
        if (student.getMainCoachId() != null) {
            Coach coach = coachMapper.selectById(student.getMainCoachId());
            vo.setMainCoachName(coach != null ? coach.getName() : "未知");
        }
        return vo;
    }
}
