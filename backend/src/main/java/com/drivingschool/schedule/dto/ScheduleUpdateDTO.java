package com.drivingschool.schedule.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 修改排班请求 DTO
 * <p>
 * 允许修改车辆、最大人数、状态、备注。
 * 最大人数不能小于当前已预约人数。
 * </p>
 */
@Data
public class ScheduleUpdateDTO {

    private Long vehicleId;

    @Min(value = 1, message = "最大预约人数必须大于0")
    private Integer maxStudents;

    private String remark;
}
