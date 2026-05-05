package com.echoofmemories.project.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.echoofmemories.project.common.Result;
import com.echoofmemories.project.dto.AdminUserListRequest;
import com.echoofmemories.project.dto.UserDetailDTO;
import com.echoofmemories.project.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员用户管理控制器
 * 
 * @author EchoOfMemories
 */
@Tag(name = "管理员用户管理", description = "管理员用户管理接口")
@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @Operation(summary = "查询用户列表")
    @PostMapping("/list")
    public Result<Page<UserDetailDTO>> getUserList(@RequestBody AdminUserListRequest request) {
        try {
            Page<UserDetailDTO> page = userService.getUserDetailPage(request);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "封禁用户")
    @PutMapping("/ban/{id}")
    public Result<String> banUser(@PathVariable Long id) {
        try {
            boolean success = userService.banUser(id);
            if (success) {
                return Result.success("封禁成功");
            } else {
                return Result.error("封禁失败");
            }
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "解封用户")
    @PutMapping("/unban/{id}")
    public Result<String> unbanUser(@PathVariable Long id) {
        try {
            boolean success = userService.unbanUser(id);
            if (success) {
                return Result.success("解封成功");
            } else {
                return Result.error("解封失败");
            }
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }
}

