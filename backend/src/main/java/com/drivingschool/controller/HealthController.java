package com.drivingschool.controller;

import com.drivingschool.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查接口 — M1 阶段临时使用
 * <p>
 * 本接口仅用于验证 Spring Boot 项目是否成功启动，不属于任何业务功能。
 * 在 M2 阶段可以保留此接口作为运维探活。
 * 启动后访问：GET http://localhost:28081/api/health
 * </p>
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    /**
     * 健康检查
     *
     * @return 固定返回 "OK"，验证服务是否正常响应
     */
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("系统启动成功", "OK");
    }
}
