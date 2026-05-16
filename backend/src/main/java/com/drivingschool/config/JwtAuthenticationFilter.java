package com.drivingschool.config;

import com.drivingschool.common.utils.JwtUserDetails;
import com.drivingschool.common.utils.JwtUtils;
import com.drivingschool.common.utils.SecurityContextUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 认证过滤器
 * <p>
 * 每个请求到达时，从 Authorization Header 中提取 Bearer Token，
 * 解析并验证 Token 有效性，然后将用户信息设置到 SecurityContext 中。
 * 如果 Token 不存在或无效，SecurityContext 为空，SecurityConfig 会拦截未认证的请求。
 * </p>
 * <p>
 * 继承 OncePerRequestFilter 确保每个请求只经过一次过滤。
 * </p>
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    /**
     * 过滤逻辑：
     * 1. 从请求头中提取 Token；
     * 2. 校验 Token 是否有效；
     * 3. 从 Token 中解析用户信息并写入 SecurityContext；
     * 4. Token 无效时不抛异常，SecurityConfig 会拦截未认证的请求并返回 401。
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);

        if (StringUtils.hasText(token) && jwtUtils.validateToken(token)) {
            Claims claims = jwtUtils.parseToken(token);
            if (claims != null) {
                // 从 Token Claims 中提取用户信息
                JwtUserDetails userDetails = new JwtUserDetails();
                userDetails.setUserId(Long.valueOf(claims.getSubject()));
                userDetails.setUsername(claims.get("username", String.class));
                userDetails.setRole(claims.get("role", String.class));
                userDetails.setRelatedId(claims.get("relatedId", Long.class));

                // 写入 SecurityContext，后续接口可通过 SecurityContextUtils 读取
                SecurityContextUtils.setAuthentication(userDetails);
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从 Authorization Header 中提取 Bearer Token
     * 格式：Authorization: Bearer <token>
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
