package com.echoofmemories.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.echoofmemories.project.common.Result;
import com.echoofmemories.project.entity.BrowseHistory;
import com.echoofmemories.project.security.SecurityUtils;
import com.echoofmemories.project.service.BrowseHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 浏览记录控制器
 */
@Tag(name = "浏览记录", description = "用户浏览记录相关接口")
@RestController
@RequestMapping("/browse")
@RequiredArgsConstructor
public class BrowseHistoryController {

    private final BrowseHistoryService browseHistoryService;

    @Operation(summary = "记录浏览历史")
    @PostMapping("/add/{productId}")
    public Result<String> addHistory(@PathVariable Long productId) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.error("401", "请先登录");
            }
            browseHistoryService.addHistory(userId, productId);
            return Result.success("记录成功");
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "获取浏览记录列表")
    @GetMapping("/list")
    public Result<IPage<BrowseHistory>> getHistoryList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) Long schoolId) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.error("401", "请先登录");
            }
            Page<BrowseHistory> page = new Page<>(current, size);
            return Result.success(browseHistoryService.getHistoryPage(page, userId, schoolId));
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "删除单条浏览记录")
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteHistory(@PathVariable Long id) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.error("401", "请先登录");
            }
            browseHistoryService.removeById(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "清空浏览记录")
    @DeleteMapping("/clear")
    public Result<String> clearHistory() {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.error("401", "请先登录");
            }
            browseHistoryService.clearHistory(userId);
            return Result.success("清空成功");
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }
}
