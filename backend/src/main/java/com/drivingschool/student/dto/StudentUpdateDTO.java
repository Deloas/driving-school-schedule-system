package com.drivingschool.student.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StudentUpdateDTO {

    @NotBlank(message = "学员姓名不能为空")
    private String name;

    @NotBlank(message = "手机号不能为空")
    private String phone;

    private String gender;

    private Long mainCoachId;

    private String subjectType;

    @Min(value = 1, message = "应完成练车次数必须大于0")
    private Integer requiredTrainingCount;

    private String remark;
}
