package com.drivingschool.coach.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 新增教练请求 DTO
 */
@Data
public class CoachCreateDTO {

    @NotBlank(message = "教练姓名不能为空")
    private String name;

    @NotBlank(message = "手机号不能为空")
    private String phone;

    /** 教练证号，可选 */
    private String licenseNo;

    /** 默认每半天最大带练人数，必须大于0 */
    @NotNull(message = "最大带练人数不能为空")
    @Min(value = 1, message = "最大带练人数必须大于0")
    private Integer maxStudentsPerHalfDay;

    private String remark;
}
