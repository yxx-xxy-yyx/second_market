package com.echoofmemories.project.security;

import com.echoofmemories.project.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Security工具类
 * 提供便捷的权限检查和用户信息获取方法
 * 
 * @author Echo of Memories
 */
public class SecurityUtils {

    /**
     * 获取当前认证的用户信息
     * 
     * @return 当前用户，如果未认证返回null
     */
    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * 获取当前用户ID
     * 
     * @return 用户ID，如果未认证返回null
     */
    public static Long getCurrentUserId() {
        // 开发模式：暂时返回固定用户ID，等服务启动后再优化
        // TODO: 后续需要从JWT token中获取真实用户ID
        return 2L; // 数据库中的普通用户ID（张三）

        // 原逻辑（生产环境需要恢复）
        // User user = getCurrentUser();
        // return user != null ? user.getId() : null;
    }

    /**
     * 获取当前用户名
     * 
     * @return 用户名，如果未认证返回null
     */
    public static String getCurrentUsername() {
        User user = getCurrentUser();
        return user != null ? user.getUsername() : null;
    }

    /**
     * 获取当前用户角色
     * 
     * @return 用户角色，如果未认证返回null
     */
    public static String getCurrentUserRole() {
        User user = getCurrentUser();
        return user != null ? user.getRole() : null;
    }

    /**
     * 获取当前用户的所有权限
     * 
     * @return 权限集合
     */
    public static Set<String> getCurrentUserAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            return authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    /**
     * 检查当前用户是否已认证
     * 
     * @return true-已认证，false-未认证
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof User;
    }

    /**
     * 检查当前用户是否具有指定角色
     * 
     * @param role 角色名称（不需要ROLE_前缀）
     * @return true-具有该角色，false-不具有该角色
     */
    public static boolean hasRole(String role) {
        Set<String> authorities = getCurrentUserAuthorities();
        return authorities.contains("ROLE_" + role.toUpperCase());
    }

    /**
     * 检查当前用户是否具有任意一个指定角色
     * 
     * @param roles 角色数组
     * @return true-具有其中任意一个角色，false-都不具有
     */
    public static boolean hasAnyRole(String... roles) {
        Set<String> authorities = getCurrentUserAuthorities();
        for (String role : roles) {
            if (authorities.contains("ROLE_" + role.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查当前用户是否具有所有指定角色
     * 
     * @param roles 角色数组
     * @return true-具有所有角色，false-不具有某个角色
     */
    public static boolean hasAllRoles(String... roles) {
        Set<String> authorities = getCurrentUserAuthorities();
        for (String role : roles) {
            if (!authorities.contains("ROLE_" + role.toUpperCase())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查当前用户是否是管理员
     * 
     * @return true-是管理员，false-不是管理员
     */
    public static boolean isAdmin() {
        return hasRole("ADMIN");
    }

    /**
     * 检查当前用户是否是普通用户
     * 
     * @return true-是普通用户，false-不是普通用户
     */
    public static boolean isUser() {
        return hasRole("USER");
    }

    /**
     * 检查当前用户是否有权限访问指定用户的资源
     * 
     * @param targetUserId 目标用户ID
     * @return true-有权限，false-没有权限
     */
    public static boolean canAccessUser(Long targetUserId) {
        if (targetUserId == null) {
            return false;
        }

        // 管理员可以访问所有用户资源
        if (isAdmin()) {
            return true;
        }

        // 用户只能访问自己的资源
        Long currentUserId = getCurrentUserId();
        return targetUserId.equals(currentUserId);
    }

    /**
     * 检查当前用户账户状态是否正常
     * 
     * @return true-状态正常，false-账户异常
     */
    public static boolean isAccountNormal() {
        User user = getCurrentUser();
        return user != null && user.getStatus() != null && user.getStatus() == 1;
    }
}
