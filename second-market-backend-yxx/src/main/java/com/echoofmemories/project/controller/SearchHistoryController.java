package com.echoofmemories.project.controller;

import com.echoofmemories.project.common.Result;
import com.echoofmemories.project.entity.SearchHistory;
import com.echoofmemories.project.security.SecurityUtils;
import com.echoofmemories.project.security.annotation.RequireRole;
import com.echoofmemories.project.service.SearchHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "搜索历史", description = "用户搜索历史相关接口")
@RestController
@RequestMapping("/search-history")
@RequiredArgsConstructor
public class SearchHistoryController {

    private final SearchHistoryService searchHistoryService;

    @Operation(summary = "记录搜索历史")
    @PostMapping("/record")
    @RequireRole({"admin", "user"})
    public Result<String> recordSearch(@RequestParam String keyword) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error("401", "请先登录");
        }
        searchHistoryService.recordSearch(userId, keyword);
        return Result.success("记录成功");
    }

    @Operation(summary = "获取当前用户搜索历史")
    @GetMapping("/list")
    @RequireRole({"admin", "user"})
    public Result<List<SearchHistory>> getHistory(@RequestParam(defaultValue = "10") Integer limit) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error("401", "请先登录");
        }
        return Result.success(searchHistoryService.getVisibleHistory(userId, limit == null ? 10 : limit));
    }

    @Operation(summary = "清空当前用户搜索历史")
    @DeleteMapping("/clear")
    @RequireRole({"admin", "user"})
    public Result<String> clearHistory() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error("401", "请先登录");
        }
        searchHistoryService.clearHistory(userId);
        return Result.success("清空成功");
    }
}
