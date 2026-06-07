package com.echoofmemories.project.controller;

import com.echoofmemories.project.common.Result;
import com.echoofmemories.project.entity.User;
import com.echoofmemories.project.security.SecurityUtils;
import com.echoofmemories.project.security.annotation.RequireRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Spring Security权限控制功能演示控制器
 * 
 * @author Echo of Memories
 */
@Tag(name = "权限控制演示", description = "演示Spring Security权限控制功能")
@RestController
@RequestMapping("/security-demo")
@RequiredArgsConstructor
public class SecurityDemoController {

    @Operation(summary = "公开访问接口")
    @GetMapping("/public")
    public Result<String> publicEndpoint() {
        return Result.success("这是一个公开访问的接口，任何人都可以访问");
    }

    @Operation(summary = "需要认证的接口")
    @GetMapping("/authenticated")
    public Result<Map<String, Object>> authenticatedEndpoint() {
        Map<String, Object> data = new HashMap<>();
        data.put("message", "你已通过认证");
        data.put("userId", SecurityUtils.getCurrentUserId());
        data.put("username", SecurityUtils.getCurrentUsername());
        data.put("role", SecurityUtils.getCurrentUserRole());
        data.put("isAdmin", SecurityUtils.isAdmin());
        data.put("authorities", SecurityUtils.getCurrentUserAuthorities());

        return Result.success("认证成功", data);
    }

    @Operation(summary = "只有管理员可以访问")
    @GetMapping("/admin-only")
    @RequireRole("admin")
    public Result<String> adminOnlyEndpoint() {
        User currentUser = SecurityUtils.getCurrentUser();
        return Result.success("管理员专用接口，当前管理员：" + currentUser.getUsername());
    }

    @Operation(summary = "只有普通用户可以访问")
    @GetMapping("/user-only")
    @RequireRole("user")
    public Result<String> userOnlyEndpoint() {
        User currentUser = SecurityUtils.getCurrentUser();
        return Result.success("普通用户专用接口，当前用户：" + currentUser.getUsername());
    }

    @Operation(summary = "管理员或用户都可以访问")
    @GetMapping("/admin-or-user")
    @RequireRole({ "admin", "user" })
    public Result<String> adminOrUserEndpoint() {
        User currentUser = SecurityUtils.getCurrentUser();
        String message = SecurityUtils.isAdmin() ? "管理员访问：" + currentUser.getUsername()
                : "普通用户访问：" + currentUser.getUsername();
        return Result.success(message);
    }

    @Operation(summary = "使用Spring Security注解")
    @GetMapping("/spring-annotation")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> springAnnotationEndpoint() {
        return Result.success("使用Spring Security @PreAuthorize注解的接口");
    }

    @Operation(summary = "复杂权限检查")
    @GetMapping("/complex-check")
    public Result<Map<String, Object>> complexPermissionCheck() {
        Map<String, Object> data = new HashMap<>();

        // 基础认证检查
        data.put("isAuthenticated", SecurityUtils.isAuthenticated());
        data.put("accountNormal", SecurityUtils.isAccountNormal());

        // 角色检查
        data.put("isAdmin", SecurityUtils.isAdmin());
        data.put("isUser", SecurityUtils.isUser());
        data.put("hasAdminRole", SecurityUtils.hasRole("admin"));
        data.put("hasUserRole", SecurityUtils.hasRole("user"));

        // 多角色检查
        data.put("hasAnyRole", SecurityUtils.hasAnyRole("admin", "user", "guest"));
        data.put("hasAllRoles", SecurityUtils.hasAllRoles("admin", "user"));

        // 用户信息
        User currentUser = SecurityUtils.getCurrentUser();
        if (currentUser != null) {
            data.put("userInfo", Map.of(
                    "id", currentUser.getId(),
                    "username", currentUser.getUsername(),
                    "nickname", currentUser.getNickname(),
                    "role", currentUser.getRole(),
                    "status", currentUser.getStatus()));
        }

        // 权限列表
        data.put("authorities", SecurityUtils.getCurrentUserAuthorities());

        return Result.success("权限检查结果", data);
    }

    @Operation(summary = "用户资源访问权限检查")
    @GetMapping("/user-access-check")
    public Result<Map<String, Boolean>> userAccessCheck() {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        Map<String, Boolean> accessResults = new HashMap<>();

        // 测试不同用户ID的访问权限
        accessResults.put("canAccessSelf", SecurityUtils.canAccessUser(currentUserId));
        accessResults.put("canAccessUser1", SecurityUtils.canAccessUser(1L));
        accessResults.put("canAccessUser2", SecurityUtils.canAccessUser(2L));
        accessResults.put("canAccessUser999", SecurityUtils.canAccessUser(999L));

        return Result.success("用户资源访问权限检查结果", accessResults);
    }
}
