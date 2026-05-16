package com.drivingschool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * 驾校学员练车预约与调度管理系统 — 启动类
 * <p>
 * 使用 IDEA 运行此文件即可启动后端服务，默认端口 28081。
 * 启动后访问 http://localhost:28081/api/health 验证服务状态。
 * </p>
 * <p>
 * 启用方法级权限控制（@EnableMethodSecurity），
 * 配合 @PreAuthorize 注解限制管理员接口只能由 ADMIN 角色调用。
 * </p>
 */
@SpringBootApplication
@EnableMethodSecurity
public class DrivingSchoolApplication {

    public static void main(String[] args) {
        SpringApplication.run(DrivingSchoolApplication.class, args);
    }
}
