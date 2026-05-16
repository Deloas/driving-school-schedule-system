package com.drivingschool.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** 管理员创建账号请求 */
@Data
public class UserCreateDTO {

    @NotBlank(message = "账号不能为空")
    private String username;

    @NotBlank(message = "角色不能为空")
    private String role; // COACH / STUDENT

    /** 绑定的教练或学员ID */
    private Long relatedId;

    /** 默认密码，不填则使用 123456 */
    private String password;
}
