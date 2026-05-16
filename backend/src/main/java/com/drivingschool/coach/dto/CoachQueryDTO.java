package com.drivingschool.coach.dto;

import lombok.Data;

/**
 * 教练查询请求 DTO
 */
@Data
public class CoachQueryDTO {

    /** 页码，从1开始 */
    private Integer page = 1;

    /** 每页条数 */
    private Integer size = 10;

    /** 搜索关键词（匹配姓名或手机号） */
    private String keyword;

    /** 状态筛选：NORMAL / LEAVE / STOPPED，为空则查全部 */
    private String status;
}
