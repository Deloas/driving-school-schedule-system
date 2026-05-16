package com.drivingschool.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求 DTO
 */
@Data
public class LoginRequestDTO {

    /** 登录账号，不能为空 */
    @NotBlank(message = "账号不能为空")
    private String username;

    /** 登录密码，不能为空 */
    @NotBlank(message = "密码不能为空")
    private String password;
}
