package com.drivingschool.schedule.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 教练排班实体 — 对应 coach_schedule 表
 * <p>
 * 每条记录表示某教练在某日期某时间段可带练的设置。
 * currentStudents 随预约创建/取消而增减，M4 阶段初始为 0。
 * </p>
 */
@Data
@TableName("coach_schedule")
public class CoachSchedule {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 教练ID */
    private Long coachId;

    /** 绑定车辆ID */
    private Long vehicleId;

    /** 排班日期 */
    private LocalDate scheduleDate;

    /** 时间段：MORNING / AFTERNOON */
    private String timeSlot;

    /** 最大预约人数 */
    private Integer maxStudents;

    /** 当前已预约人数 */
    private Integer currentStudents;

    /** 状态：OPEN / CLOSED / CANCELLED */
    private String status;

    /** 备注 */
    private String remark;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
