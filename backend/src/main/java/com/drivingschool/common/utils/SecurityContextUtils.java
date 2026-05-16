package com.drivingschool.common.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import java.util.Collections;
import java.util.List;

/**
 * 安全上下文工具类
 * <p>
 * 用于在业务代码中获取当前登录用户的信息，以及手动设置认证状态。
 * 登录成功后 JwtAuthenticationFilter 会将用户信息写入 SecurityContext，
 * 后续 Controller/Service 可通过本工具类读取当前操作用户。
 * </p>
 */
public class SecurityContextUtils {

    private SecurityContextUtils() {}

    /**
     * 获取当前登录用户的用户ID
     *
     * @return 用户ID，未登录时返回 null
     */
    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof JwtUserDetails userDetails) {
            return userDetails.getUserId();
        }
        return null;
    }

    /**
     * 获取当前登录用户的角色
     *
     * @return 角色字符串，未登录时返回 null
     */
    public static String getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof JwtUserDetails userDetails) {
            return userDetails.getRole();
        }
        return null;
    }

    /**
     * 获取当前登录用户的完整信息
     *
     * @return JwtUserDetails，未登录时返回 null
     */
    public static JwtUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof JwtUserDetails userDetails) {
            return userDetails;
        }
        return null;
    }

    /**
     * 手动设置当前请求的认证上下文
     * <p>
     * Spring Security 的 hasRole('ADMIN') 实际检查的是 ROLE_ADMIN 权限名，
     * 因此必须将角色拼接为 "ROLE_" + role 存入 GrantedAuthority。
     * hasRole 方法会自动在参数前拼接 ROLE_ 前缀进行比对。
     * </p>
     */
    public static void setAuthentication(JwtUserDetails userDetails) {
        // Spring Security hasRole('ADMIN') 实际校验 authority == 'ROLE_ADMIN'
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + userDetails.getRole())
        );
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    /** 清除当前请求的认证上下文（退出登录时使用） */
    public static void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }
}
