package com.echoofmemories.project.controller;

import com.echoofmemories.project.common.Result;
import com.echoofmemories.project.dto.UpdateAvatarRequest;
import com.echoofmemories.project.dto.UserInfoUpdateRequest;
import com.echoofmemories.project.dto.UserProfileDTO;
import com.echoofmemories.project.entity.User;
import com.echoofmemories.project.security.SecurityUtils;
import com.echoofmemories.project.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 用户控制器
 *
 * @author EchoOfMemories
 */
@Tag(name = "用户管理", description = "用户相关接口")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "获取当前登录用户信息（可信身份）")
    @GetMapping("/me")
    public Result<UserProfileDTO> getCurrentUserProfile(HttpServletRequest request) {
        try {
            // 完全依赖后端认证信息获取用户ID，不可由前端伪造
            Long currentUserId = (Long) request.getAttribute("userId");
            if (currentUserId == null) {
                currentUserId = SecurityUtils.getCurrentUserId();
            }

            if (currentUserId == null) {
                return Result.error("401", "用户未认证");
            }

            User user = userService.getById(currentUserId);
            if (user == null) {
                return Result.error("404", "用户不存在");
            }

            // 组装可信 DTO，从数据库与 token 解析结果中取字段
            UserProfileDTO profile = new UserProfileDTO();
            profile.setId(user.getId());
            profile.setUsername(user.getUsername());
            profile.setNickname(user.getNickname());
            profile.setEmail(user.getEmail());
            profile.setAvatar(user.getAvatar());
            // role 字段由后端认证信息注入，不从请求参数接受
            String role = SecurityUtils.getCurrentUserRole();
            profile.setRole(role != null ? role : user.getRole());
            profile.setSchoolId(user.getSchoolId());
            profile.setCreditScore(user.getCreditScore());
            profile.setStatus(user.getStatus());

            return Result.success(profile);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    // 该接口保留，仅供管理员查看他人信息场景使用；普通用户请使用 GET /user/me
    // 为避免前端伪造 userId，这里将原 /user/info 标注为管理员专用
    @Operation(summary = "[管理员专用] 获取指定用户信息")
    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestParam(value = "userId", required = false) Long userId) {
        try {
            // 1. 必须是管理员才能查看他人信息
            if (!SecurityUtils.isAdmin()) {
                return Result.error("403", "无权查看他人信息，请使用 GET /user/me 获取自己的信息");
            }

            // 2. 逻辑判断：如果传入了 userId 则用传入的，否则用当前登录的
            Long queryId = (userId != null) ? userId : SecurityUtils.getCurrentUserId();

            if (queryId == null) {
                return Result.error("401", "未获取到有效用户ID");
            }
            User user = userService.getById(queryId);
            if (user != null) {
                user.setPassword(null); // 安全处理，脱敏
            }
            return Result.success(user);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/info")
    public Result<String> updateUserInfo(@Valid @RequestBody UserInfoUpdateRequest request) {
        try {
            // 开发模式：注释掉权限验证，使用SecurityUtils返回的用户ID
            Long userId = SecurityUtils.getCurrentUserId();
            // if (userId == null) {
            // return Result.error("401", "用户未认证");
            // }

            boolean success = userService.updateUserInfo(userId, request);
            if (success) {
                return Result.success("更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "更新用户头像")
    @PutMapping("/avatar")
    public Result<String> updateAvatar(@Valid @RequestBody UpdateAvatarRequest request) {
        try {
            // 开发模式：注释掉权限验证，使用SecurityUtils返回的用户ID
            Long userId = SecurityUtils.getCurrentUserId();
            // if (userId == null) {
            // return Result.error("401", "用户未认证");
            // }

            boolean success = userService.updateAvatar(userId, request.getAvatarUrl());
            if (success) {
                return Result.success("头像更新成功");
            } else {
                return Result.error("头像更新失败");
            }
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "更新用户资料")
    @PutMapping("/profile")
    public Result<String> updateUserProfile(@Valid @RequestBody UserInfoUpdateRequest request) {
        try {
            // 开发模式：注释掉权限验证，使用SecurityUtils返回的用户ID
            Long userId = SecurityUtils.getCurrentUserId();
            // if (userId == null) {
            // return Result.error("401", "用户未认证");
            // }

            boolean success = userService.updateUserInfo(userId, request);
            if (success) {
                return Result.success("更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }
}
