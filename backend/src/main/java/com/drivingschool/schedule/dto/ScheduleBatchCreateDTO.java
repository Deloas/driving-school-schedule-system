package com.drivingschool.schedule.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 批量生成排班请求 DTO
 */
@Data
public class ScheduleBatchCreateDTO {

    @NotEmpty(message = "教练列表不能为空")
    private List<Long> coachIds;

    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;

    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;

    @NotEmpty(message = "时间段不能为空")
    private List<String> timeSlots;

    @Min(value = 1, message = "最大预约人数必须大于0")
    private Integer maxStudents;

    /** 是否自动绑定教练默认车辆 */
    private Boolean autoBindVehicle = false;

    private String remark;
}
