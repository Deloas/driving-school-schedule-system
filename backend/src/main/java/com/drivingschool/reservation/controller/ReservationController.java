package com.drivingschool.reservation.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drivingschool.common.result.Result;
import com.drivingschool.common.utils.SecurityContextUtils;
import com.drivingschool.reservation.dto.ReservationCancelDTO;
import com.drivingschool.reservation.dto.ReservationCreateDTO;
import com.drivingschool.reservation.dto.ReservationQueryDTO;
import com.drivingschool.reservation.service.ReservationService;
import com.drivingschool.reservation.vo.ReservationOptionVO;
import com.drivingschool.reservation.vo.ReservationVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 预约管理控制器
 * <p>
 * 统一 reservation 模块，不按角色拆分。
 * 权限通过 @PreAuthorize + SecurityContextUtils 区分。
 * </p>
 */
@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    /** 查询可预约方案 — 学员端 */
    @GetMapping("/options")
    public Result<ReservationOptionVO> options(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String timeSlot) {
        Long studentId = SecurityContextUtils.getCurrentUser().getRelatedId();
        return Result.success(reservationService.getOptions(studentId, date, timeSlot));
    }

    /** 创建预约 — 仅学员 */
    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public Result<ReservationVO> create(@Valid @RequestBody ReservationCreateDTO dto) {
        Long studentId = SecurityContextUtils.getCurrentUser().getRelatedId();
        return Result.success("预约成功", reservationService.create(dto, studentId));
    }

    /** 取消预约 — 学员可取消自己的，管理员可取消任意 */
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    public Result<String> cancel(@PathVariable Long id, @RequestBody(required = false) ReservationCancelDTO dto) {
        if (dto == null) dto = new ReservationCancelDTO();
        Long operatorId = SecurityContextUtils.getCurrentUser().getRelatedId();
        String role = SecurityContextUtils.getCurrentUserRole();
        reservationService.cancel(id, dto, operatorId, role);
        return Result.success("取消预约成功");
    }

    /** 查询我的预约 — 仅学员 */
    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<Page<ReservationVO>> my(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long studentId = SecurityContextUtils.getCurrentUser().getRelatedId();
        return Result.success(reservationService.myReservations(studentId, page, size));
    }

    /** 教练查询今日预约 — 仅教练 */
    @GetMapping("/coach/today")
    @PreAuthorize("hasRole('COACH')")
    public Result<Page<ReservationVO>> coachToday(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        Long coachId = SecurityContextUtils.getCurrentUser().getRelatedId();
        return Result.success(reservationService.coachToday(coachId, date, page, size));
    }

    /** 管理员分页查询全部预约 */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<ReservationVO>> list(ReservationQueryDTO dto) {
        return Result.success(reservationService.adminQuery(dto));
    }
}
