package com.drivingschool.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drivingschool.common.result.Result;
import com.drivingschool.user.dto.ResetPasswordDTO;
import com.drivingschool.user.dto.UserCreateDTO;
import com.drivingschool.user.dto.UserQueryDTO;
import com.drivingschool.user.service.UserService;
import com.drivingschool.user.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 账号管理控制器 — 仅管理员
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    @GetMapping
    public Result<Page<UserVO>> list(UserQueryDTO dto) {
        return Result.success(userService.queryPage(dto));
    }

    @PostMapping
    public Result<UserVO> create(@Valid @RequestBody UserCreateDTO dto) {
        UserVO vo = userService.create(dto);
        return Result.success("账号创建成功，默认密码 123456", vo);
    }

    @PatchMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        userService.updateStatus(id, body.get("status"));
        return Result.success("状态修改成功");
    }

    @PatchMapping("/{id}/password/reset")
    public Result<String> resetPassword(@PathVariable Long id, @RequestBody(required = false) ResetPasswordDTO dto) {
        if (dto == null) dto = new ResetPasswordDTO();
        String newPwd = userService.resetPassword(id, dto);
        return Result.success("密码已重置为: " + newPwd);
    }
}
