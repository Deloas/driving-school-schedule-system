package com.drivingschool.schedule.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 新增排班请求 DTO
 */
@Data
public class ScheduleCreateDTO {

    @NotNull(message = "教练不能为空")
    private Long coachId;

    /** 绑定车辆，可选 */
    private Long vehicleId;

    @NotNull(message = "排班日期不能为空")
    private LocalDate scheduleDate;

    @NotBlank(message = "时间段不能为空")
    private String timeSlot;

    @Min(value = 1, message = "最大预约人数必须大于0")
    private Integer maxStudents;

    private String remark;
}
