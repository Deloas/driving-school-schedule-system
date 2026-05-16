package com.drivingschool.student.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StudentCreateDTO {

    @NotBlank(message = "学员姓名不能为空")
    private String name;

    @NotBlank(message = "手机号不能为空")
    private String phone;

    private String gender;

    /** 主教练ID，必填 */
    @NotNull(message = "主教练不能为空")
    private Long mainCoachId;

    /** 科目类型，默认 SUBJECT_2 */
    private String subjectType = "SUBJECT_2";

    /** 应完成练车次数，默认 8 */
    @Min(value = 1, message = "应完成练车次数必须大于0")
    private Integer requiredTrainingCount = 8;

    private String remark;
}
