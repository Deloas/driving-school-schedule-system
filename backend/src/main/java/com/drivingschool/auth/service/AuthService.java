package com.drivingschool.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.drivingschool.auth.dto.LoginRequestDTO;
import com.drivingschool.auth.vo.LoginResponseVO;
import com.drivingschool.auth.vo.UserInfoVO;
import com.drivingschool.common.exception.BusinessException;
import com.drivingschool.common.utils.JwtUserDetails;
import com.drivingschool.common.utils.JwtUtils;
import com.drivingschool.entity.SysUser;
import com.drivingschool.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务 — 处理登录验证和用户信息查询
 * <p>
 * M2 阶段承担登录校验、Token 生成、用户信息查询职责。
 * 不包含注册功能（系统管理员在后台直接创建账号）。
 * </p>
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    /**
     * 用户登录
     * <p>
     * 验证流程：
     * 1. 根据 username 查询 sys_user 表；
     * 2. 校验账号是否存在、是否被禁用；
     * 3. 使用 BCrypt 校验密码是否匹配；
     * 4. 生成 JWT Token 并返回。
     * </p>
     *
     * @param dto 登录请求（username + password）
     * @return LoginResponseVO（token + 用户信息）
     */
    public LoginResponseVO login(LoginRequestDTO dto) {
        // 1. 查询用户
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, dto.getUsername())
        );

        // 2. 校验账号是否存在
        if (user == null) {
            throw new BusinessException("账号或密码错误");
        }

        // 3. 校验账号是否被禁用
        if ("DISABLE".equals(user.getStatus())) {
            throw new BusinessException("该账号已被禁用，请联系管理员");
        }

        // 4. 使用 BCrypt 校验密码（数据库中存储的是 BCrypt 密文）
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }

        // 5. 生成 JWT Token
        String token = jwtUtils.generateToken(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.getRelatedId()
        );

        // 6. 组装响应
        return LoginResponseVO.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .relatedId(user.getRelatedId())
                .build();
    }

    /**
     * 查询当前登录用户的信息
     * <p>
     * 从 SecurityContext 中获取当前用户 ID，然后从数据库查询最新状态。
     * 这让前端可以在刷新页面后验证 Token 是否仍然有效。
     * </p>
     *
     * @param userId 当前登录用户ID（从 Token 中解析）
     */
    public UserInfoVO getCurrentUser(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        return UserInfoVO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .relatedId(user.getRelatedId())
                .status(user.getStatus())
                .build();
    }
}
