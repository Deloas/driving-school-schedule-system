package com.drivingschool.schedule.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drivingschool.common.result.Result;
import com.drivingschool.common.utils.SecurityContextUtils;
import com.drivingschool.schedule.dto.ScheduleBatchCreateDTO;
import com.drivingschool.schedule.dto.ScheduleCreateDTO;
import com.drivingschool.schedule.dto.ScheduleQueryDTO;
import com.drivingschool.schedule.dto.ScheduleUpdateDTO;
import com.drivingschool.schedule.service.CoachScheduleService;
import com.drivingschool.schedule.vo.ScheduleAvailableVO;
import com.drivingschool.schedule.vo.ScheduleVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 教练排班管理控制器
 */
@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class CoachScheduleController {

    private final CoachScheduleService scheduleService;

    /** 分页查询排班 — 教练只能看自己的 */
    @GetMapping
    public Result<Page<ScheduleVO>> list(ScheduleQueryDTO dto) {
        if ("COACH".equals(SecurityContextUtils.getCurrentUserRole())) {
            dto.setCoachId(SecurityContextUtils.getCurrentUser().getRelatedId());
        }
        return Result.success(scheduleService.queryPage(dto));
    }

    /** 查询可预约排班（学员端使用） */
    @GetMapping("/available")
    public Result<List<ScheduleAvailableVO>> available(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String timeSlot) {
        return Result.success(scheduleService.availableList(date, timeSlot));
    }

    @GetMapping("/{id}")
    public Result<ScheduleVO> detail(@PathVariable Long id) {
        return Result.success(scheduleService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<ScheduleVO> create(@Valid @RequestBody ScheduleCreateDTO dto) {
        return Result.success("新增排班成功", scheduleService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<ScheduleVO> update(@PathVariable Long id, @Valid @RequestBody ScheduleUpdateDTO dto) {
        return Result.success("修改排班成功", scheduleService.update(id, dto));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        scheduleService.updateStatus(id, body.get("status"));
        return Result.success("状态修改成功");
    }

    /** 批量生成排班 — 仅管理员 */
    @PostMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Map<String, Integer>> batchCreate(@Valid @RequestBody ScheduleBatchCreateDTO dto) {
        Map<String, Integer> result = scheduleService.batchCreate(dto);
        return Result.success(String.format("成功创建 %d 条，跳过 %d 条", result.get("created"), result.get("skipped")), result);
    }
}
