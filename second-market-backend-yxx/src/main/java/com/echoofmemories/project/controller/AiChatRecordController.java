package com.echoofmemories.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.echoofmemories.project.common.Result;
import com.echoofmemories.project.entity.AiChatRecord;
import com.echoofmemories.project.security.SecurityUtils;
import com.echoofmemories.project.security.annotation.RequireRole;
import com.echoofmemories.project.service.AiChatRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "AI聊天记录管理", description = "AI 聊天记录的查询与清理")
@RestController
@RequestMapping("/ai/chat")
@RequiredArgsConstructor
public class AiChatRecordController {

    private final AiChatRecordService aiChatRecordService;

    @Operation(summary = "获取AI聊天记录")
    @GetMapping("/history")
    @RequireRole({"admin", "user"})
    public Result<List<AiChatRecord>> getHistory() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error("未登录");
        }

        LambdaQueryWrapper<AiChatRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiChatRecord::getUserId, userId)
                .orderByAsc(AiChatRecord::getCreateTime);

        return Result.success(aiChatRecordService.list(wrapper));
    }

    @Operation(summary = "清空AI聊天记录")
    @DeleteMapping("/history")
    @RequireRole({"admin", "user"})
    public Result<Boolean> clearHistory() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error("未登录");
        }

        LambdaQueryWrapper<AiChatRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiChatRecord::getUserId, userId);

        return Result.success(aiChatRecordService.remove(wrapper));
    }
}
