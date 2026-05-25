package com.echoofmemories.project.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.echoofmemories.project.common.Result;
import com.echoofmemories.project.dto.PageRequest;
import com.echoofmemories.project.entity.FileInfo;
import com.echoofmemories.project.entity.User;
import com.echoofmemories.project.dto.AdminUserListRequest;
import com.echoofmemories.project.dto.UserDetailDTO;
import com.echoofmemories.project.service.FileService;
import com.echoofmemories.project.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员控制器
 * 
 * @author Echo of Memories
 */
@Tag(name = "管理员管理", description = "管理员相关接口")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final UserService userService;
    private final FileService fileService;
    
    @Operation(summary = "获取系统概览统计")
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        
        // 用户统计
        long totalUsers = userService.count();
        long activeUsers = userService.lambdaQuery().eq(User::getStatus, 1).count();
        
        // 文件统计
        long totalFiles = fileService.count();
        long imageFiles = fileService.lambdaQuery().eq(FileInfo::getFileType, "image").count();
        long documentFiles = fileService.lambdaQuery().eq(FileInfo::getFileType, "document").count();
        
        dashboard.put("totalUsers", totalUsers);
        dashboard.put("activeUsers", activeUsers);
        dashboard.put("inactiveUsers", totalUsers - activeUsers);
        dashboard.put("totalFiles", totalFiles);
        dashboard.put("imageFiles", imageFiles);
        dashboard.put("documentFiles", documentFiles);
        dashboard.put("otherFiles", totalFiles - imageFiles - documentFiles);
        
        return Result.success(dashboard);
    }
    
    @Operation(summary = "管理员获取用户列表")
    @GetMapping("/users")
    public Result<Page<User>> getUserList(PageRequest pageRequest) {
        Page<User> userPage = userService.getUserPage(pageRequest);
        // 清除密码字段
        userPage.getRecords().forEach(user -> user.setPassword(null));
        return Result.success(userPage);
    }

    @Operation(summary = "查询用户详情列表")
    @PostMapping("/user/list")
    public Result<Page<UserDetailDTO>> getUserDetailPage(@RequestBody AdminUserListRequest request) {
        try {
            Page<UserDetailDTO> page = userService.getUserDetailPage(request);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "封禁用户")
    @PutMapping("/user/ban/{id}")
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
    @PutMapping("/user/unban/{id}")
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
    
    @Operation(summary = "管理员创建用户")
    @PostMapping("/users")
    public Result<User> createUser(@RequestBody User user) {
        User newUser = userService.register(user);
        return Result.success("用户创建成功", newUser);
    }
    
    @Operation(summary = "管理员更新用户信息")
    @PutMapping("/users/{id}")
    public Result<String> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        boolean updated = userService.updateById(user);
        if (updated) {
            return Result.success("用户信息更新成功");
        } else {
            return Result.error("用户信息更新失败");
        }
    }
    
    @Operation(summary = "管理员删除用户")
    @DeleteMapping("/users/{id}")
    public Result<String> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.removeById(id);
        if (deleted) {
            return Result.success("用户删除成功");
        } else {
            return Result.error("用户删除失败");
        }
    }
    
    @Operation(summary = "管理员批量删除用户")
    @DeleteMapping("/users/batch")
    public Result<String> batchDeleteUsers(@RequestBody List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Result.error("请选择要删除的用户");
        }
        boolean deleted = userService.removeByIds(userIds);
        if (deleted) {
            return Result.success("批量删除成功，共删除 " + userIds.size() + " 个用户");
        } else {
            return Result.error("批量删除失败");
        }
    }
    
    @Operation(summary = "管理员更新用户状态")
    @PutMapping("/users/{id}/status")
    public Result<String> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean updated = userService.updateStatus(id, status);
        if (updated) {
            return Result.success("用户状态更新成功");
        } else {
            return Result.error("用户状态更新失败");
        }
    }
    
    @Operation(summary = "管理员重置用户密码")
    @PutMapping("/users/{id}/reset-password")
    public Result<String> resetUserPassword(@PathVariable Long id, @RequestParam String newPassword) {
        boolean reset = userService.resetPassword(id, newPassword);
        if (reset) {
            return Result.success("密码重置成功");
        } else {
            return Result.error("密码重置失败");
        }
    }
    
    @Operation(summary = "管理员获取文件列表")
    @GetMapping("/files")
    public Result<Page<FileInfo>> getFileList(PageRequest pageRequest,
                                             @RequestParam(value = "userId", required = false) Long userId,
                                             @RequestParam(value = "fileType", required = false) String fileType) {
        Page<FileInfo> filePage = fileService.getFilePage(pageRequest, userId, fileType);
        return Result.success(filePage);
    }
    
    @Operation(summary = "管理员删除文件")
    @DeleteMapping("/files/{id}")
    public Result<String> deleteFile(@PathVariable Long id) {
        // 管理员可以删除任意文件，这里传入一个特殊的管理员ID
        boolean deleted = fileService.removeById(id);
        if (deleted) {
            return Result.success("文件删除成功");
        } else {
            return Result.error("文件删除失败");
        }
    }
    
    @Operation(summary = "获取系统信息")
    @GetMapping("/system/info")
    public Result<Map<String, Object>> getSystemInfo() {
        Map<String, Object> systemInfo = new HashMap<>();
        systemInfo.put("systemName", "Echo of Memories Project");
        systemInfo.put("version", "1.0.0");
        systemInfo.put("javaVersion", System.getProperty("java.version"));
        systemInfo.put("osName", System.getProperty("os.name"));
        systemInfo.put("osArch", System.getProperty("os.arch"));
        
        // 内存信息
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        Map<String, Object> memoryInfo = new HashMap<>();
        memoryInfo.put("total", totalMemory / 1024 / 1024 + " MB");
        memoryInfo.put("used", usedMemory / 1024 / 1024 + " MB");
        memoryInfo.put("free", freeMemory / 1024 / 1024 + " MB");
        
        systemInfo.put("memory", memoryInfo);
        
        return Result.success(systemInfo);
    }
    
}