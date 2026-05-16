package com.drivingschool.student.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drivingschool.student.dto.StudentCreateDTO;
import com.drivingschool.student.dto.StudentQueryDTO;
import com.drivingschool.student.dto.StudentUpdateDTO;
import com.drivingschool.student.vo.StudentSimpleVO;
import com.drivingschool.student.vo.StudentVO;

import java.util.List;

public interface StudentService {
    Page<StudentVO> queryPage(StudentQueryDTO dto);
    StudentVO getById(Long id);
    StudentVO create(StudentCreateDTO dto);
    StudentVO update(Long id, StudentUpdateDTO dto);
    void updateStatus(Long id, String status);
    List<StudentSimpleVO> simpleList();
}
