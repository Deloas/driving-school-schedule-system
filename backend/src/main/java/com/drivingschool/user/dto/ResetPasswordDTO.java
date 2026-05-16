package com.drivingschool.user.dto;

import lombok.Data;

/** 重置密码请求 */
@Data
public class ResetPasswordDTO {
    /** 新密码，不填则重置为 123456 */
    private String newPassword;
}
