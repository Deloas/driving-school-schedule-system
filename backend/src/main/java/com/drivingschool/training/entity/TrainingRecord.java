package com.drivingschool.training.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 练车记录实体 — 对应 training_record 表
 */
@Data
@TableName("training_record")
public class TrainingRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联预约ID */
    private Long reservationId;

    /** 学员ID */
    private Long studentId;

    /** 实际带练教练ID */
    private Long coachId;

    /** 车辆ID */
    private Long vehicleId;

    /** 练车日期 */
    private LocalDate trainingDate;

    /** 时间段 */
    private String timeSlot;

    /** 训练内容 */
    private String trainingContent;

    /** 结果：COMPLETED / ABSENT */
    private String result;

    /** 教练备注 */
    private String coachComment;

    private LocalDateTime createTime;
}
