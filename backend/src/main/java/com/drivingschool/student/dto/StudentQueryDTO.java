package com.drivingschool.student.dto;

import lombok.Data;

@Data
public class StudentQueryDTO {
    private Integer page = 1;
    private Integer size = 10;
    /** 搜索关键词（姓名或手机号） */
    private String keyword;
    /** 按主教练筛选 */
    private Long coachId;
    /** 状态筛选 */
    private String status;
}
