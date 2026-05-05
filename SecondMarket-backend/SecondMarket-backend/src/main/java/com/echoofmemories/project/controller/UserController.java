package com.echoofmemories.project.controller;

import com.echoofmemories.project.common.Result;
import com.echoofmemories.project.dto.UpdateAvatarRequest;
import com.echoofmemories.project.dto.UserInfoUpdateRequest;
import com.echoofmemories.project.entity.User;
import com.echoofmemories.project.security.SecurityUtils;
import com.echoofmemories.project.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result<User> getUserInfo() {
        try {
            // 开发模式：注释掉权限验证，使用SecurityUtils返回的用户ID
            Long userId = SecurityUtils.getCurrentUserId();
            // if (userId == null) {
            // return Result.error("401", "用户未认证");
            // }

            User user = userService.getById(userId);
            if (user != null) {
                user.setPassword(null);
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
