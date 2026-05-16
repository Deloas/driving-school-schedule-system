package com.drivingschool.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统用户实体 — 对应 sys_user 表
 * <p>
 * 存储所有角色的登录账号和加密密码。
 * 通过 role 字段区分 ADMIN/COACH/STUDENT，通过 related_id 关联到学员表或教练表。
 * </p>
 */
@Data
@TableName("sys_user")
public class SysUser {

    /** 用户ID，自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 登录账号，唯一 */
    private String username;

    /** BCrypt 加密后的密码 */
    private String password;

    /** 角色：ADMIN / COACH / STUDENT */
    private String role;

    /** 关联业务ID（学员ID 或 教练ID），管理员可为空 */
    private Long relatedId;

    /** 状态：ENABLE / DISABLE */
    private String status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
