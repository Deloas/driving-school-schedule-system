package com.drivingschool.student.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drivingschool.common.result.Result;
import com.drivingschool.common.utils.SecurityContextUtils;
import com.drivingschool.student.dto.StudentCreateDTO;
import com.drivingschool.student.dto.StudentQueryDTO;
import com.drivingschool.student.dto.StudentUpdateDTO;
import com.drivingschool.student.service.StudentService;
import com.drivingschool.student.vo.StudentSimpleVO;
import com.drivingschool.student.vo.StudentVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 学员管理控制器
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    /** 分页查询学员 */
    @GetMapping("/students")
    public Result<Page<StudentVO>> list(StudentQueryDTO dto) {
        // 教练只能查看自己名下的学员
        if ("COACH".equals(SecurityContextUtils.getCurrentUserRole())) {
            dto.setCoachId(SecurityContextUtils.getCurrentUser().getRelatedId());
        }
        // 学员只能查看自己的信息
        if ("STUDENT".equals(SecurityContextUtils.getCurrentUserRole())) {
            Long studentId = SecurityContextUtils.getCurrentUser().getRelatedId();
            StudentVO vo = studentService.getById(studentId);
            Page<StudentVO> page = new Page<>(1, 1, 1);
            page.setRecords(List.of(vo));
            return Result.success(page);
        }
        return Result.success(studentService.queryPage(dto));
    }

    /** 查询某教练名下学员 */
    @GetMapping("/coaches/{coachId}/students")
    public Result<Page<StudentVO>> listByCoach(@PathVariable Long coachId, StudentQueryDTO dto) {
        dto.setCoachId(coachId);
        return Result.success(studentService.queryPage(dto));
    }

    /** M9.5-B：教练查看自己绑定的学员 */
    @GetMapping("/students/my")
    @PreAuthorize("hasRole('COACH')")
    public Result<Page<StudentVO>> myStudents(StudentQueryDTO dto) {
        dto.setCoachId(SecurityContextUtils.getCurrentUser().getRelatedId());
        return Result.success(studentService.queryPage(dto));
    }

    @GetMapping("/students/simple")
    public Result<List<StudentSimpleVO>> simpleList() {
        return Result.success(studentService.simpleList());
    }

    @GetMapping("/students/{id}")
    public Result<StudentVO> detail(@PathVariable Long id) {
        return Result.success(studentService.getById(id));
    }

    /** 新增学员 — 仅管理员 */
    @PostMapping("/students")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<StudentVO> create(@Valid @RequestBody StudentCreateDTO dto) {
        return Result.success("新增学员成功", studentService.create(dto));
    }

    /** 修改学员 — 仅管理员 */
    @PutMapping("/students/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<StudentVO> update(@PathVariable Long id, @Valid @RequestBody StudentUpdateDTO dto) {
        return Result.success("修改学员成功", studentService.update(id, dto));
    }

    /** 修改学员状态 — 仅管理员 */
    @PatchMapping("/students/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        studentService.updateStatus(id, body.get("status"));
        return Result.success("状态修改成功");
    }
}
