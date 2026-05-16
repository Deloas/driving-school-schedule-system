package com.drivingschool.vehicle.dto;

import lombok.Data;

@Data
public class VehicleQueryDTO {
    private Integer page = 1;
    private Integer size = 10;
    /** 搜索关键词（车牌号） */
    private String keyword;
    /** 按绑定教练筛选 */
    private Long coachId;
    /** 状态筛选 */
    private String status;
}
