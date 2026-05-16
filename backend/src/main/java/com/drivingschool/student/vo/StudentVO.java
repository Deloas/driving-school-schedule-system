package com.drivingschool.student.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StudentVO {
    private Long id;
    private String name;
    private String phone;
    private String gender;
    private Long mainCoachId;
    /** 主教练姓名，联表或二次查询填充 */
    private String mainCoachName;
    private String subjectType;
    private Integer requiredTrainingCount;
    private Integer completedTrainingCount;
    private String status;
    private String remark;
    private LocalDateTime createTime;
}
