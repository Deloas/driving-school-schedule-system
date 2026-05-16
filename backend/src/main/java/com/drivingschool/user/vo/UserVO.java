package com.drivingschool.user.vo;

import lombok.Data;
import java.time.LocalDateTime;

/** 账号详情响应 */
@Data
public class UserVO {
    private Long id;
    private String username;
    private String role;
    private Long relatedId;
    /** 绑定对象的名称 */
    private String relatedName;
    private String status;
    private LocalDateTime createTime;
}
