package com.drivingschool.auth.controller;

import com.drivingschool.auth.dto.LoginRequestDTO;
import com.drivingschool.auth.service.AuthService;
import com.drivingschool.auth.vo.LoginResponseVO;
import com.drivingschool.auth.vo.UserInfoVO;
import com.drivingschool.common.result.Result;
import com.drivingschool.common.utils.SecurityContextUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器 — 处理登录和用户信息请求
 * <p>
 * M2 阶段只实现 login 和 me 两个接口。
 * login 无需认证即可访问，me 需要携带有效 JWT Token。
 * </p>
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     * <p>
     * 请求体：{ "username": "student001", "password": "123456" }
     * 成功返回 Token + 用户信息，前端据此跳转到对应角色首页。
     * </p>
     */
    @PostMapping("/login")
    public Result<LoginResponseVO> login(@Valid @RequestBody LoginRequestDTO dto) {
        LoginResponseVO vo = authService.login(dto);
        return Result.success("登录成功", vo);
    }

    /**
     * 获取当前登录用户信息
     * <p>
     * 前端在页面刷新时调用此接口，验证 Token 是否仍然有效并获取最新用户信息。
     * 需要在请求头中携带 Authorization: Bearer <token>。
     * </p>
     */
    @GetMapping("/me")
    public Result<UserInfoVO> me() {
        Long userId = SecurityContextUtils.getCurrentUserId();
        UserInfoVO vo = authService.getCurrentUser(userId);
        return Result.success(vo);
    }
}
