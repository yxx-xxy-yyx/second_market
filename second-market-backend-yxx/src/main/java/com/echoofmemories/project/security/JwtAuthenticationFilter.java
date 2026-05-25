package com.echoofmemories.project.security;

import com.echoofmemories.project.entity.User;
import com.echoofmemories.project.service.UserService;
import com.echoofmemories.project.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.util.Collections;

/**
 * JWT认证过滤器
 * 
 * @author Echo of Memories
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String userId = null;

        // 检查是否包含Bearer token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                // 验证token并获取用户ID
                if (JwtUtils.verify(token)) {
                    userId = JwtUtils.getUserId(token);
                    // 【核心修复】：必须将解析出的 userId 存入 request
                    // 这样 Controller 里的 getCurrentUserId 才能拿到
                    request.setAttribute("userId", userId);
                }
            } catch (Exception e) {
                log.error("JWT token解析失败: {}", e.getMessage());
            }
        }

        // 如果token有效且上下文中没有认证信息，则设置认证
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // 根据用户ID获取用户详细信息
                User user = userService.getById(Long.valueOf(userId));

                if (user != null && user.getStatus() != null && user.getStatus() == 1) {
                    // 创建权限列表
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(
                            "ROLE_" + user.getRole().toUpperCase());

                    // 创建认证对象
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null,
                            Collections.singletonList(authority));

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    log.debug("用户认证成功: userId={}, role={}", userId, user.getRole());
                }
            } catch (Exception e) {
                log.error("用户认证过程中发生错误: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
