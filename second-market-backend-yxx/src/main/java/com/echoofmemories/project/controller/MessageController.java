package com.echoofmemories.project.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.echoofmemories.project.common.Result;
import com.echoofmemories.project.dto.MessageSendRequest;
import com.echoofmemories.project.entity.Message;
import com.echoofmemories.project.security.SecurityUtils;
import com.echoofmemories.project.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 消息控制器
 * 
 * @author EchoOfMemories
 */
@Tag(name = "消息管理", description = "消息相关接口")
@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @Operation(summary = "发送消息")
    @PostMapping("/send")
    public Result<String> sendMessage(@Valid @RequestBody MessageSendRequest request) {
        try {
            boolean success = messageService.sendMessage(request);
            return success ? Result.success("发送成功") : Result.error("发送失败");
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "群发消息")
    @PostMapping("/send/all")
    public Result<String> sendMessageToAll(@Valid @RequestBody MessageSendRequest request) {
        try {
            boolean success = messageService.sendMessageToAll(request);
            return success ? Result.success("群发成功") : Result.error("群发失败");
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "我的消息列表")
    @PostMapping("/my")
    public Result<Page<Message>> getMyMessages(@RequestParam(defaultValue = "1") Integer pageNum,
                                                @RequestParam(defaultValue = "10") Integer pageSize,
                                                @RequestParam(required = false) Integer isRead) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.error("401", "用户未认证");
            }

            Page<Message> page = messageService.getMyMessagePage(pageNum, pageSize, userId, isRead);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "标记消息已读")
    @PutMapping("/read/{id}")
    public Result<String> markAsRead(@PathVariable Long id) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.error("401", "用户未认证");
            }

            boolean success = messageService.markAsRead(id, userId);
            return success ? Result.success("标记成功") : Result.error("标记失败");
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "获取未读消息数量")
    @GetMapping("/unread/count")
    public Result<Long> getUnreadCount() {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.success(0L);
            }

            Long count = messageService.getUnreadCount(userId);
            return Result.success(count);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "获取消息详情")
    @GetMapping("/{id}")
    public Result<Message> getMessageById(@PathVariable Long id) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) return Result.error("401", "用户未认证");
            Message message = messageService.getById(id);
            if (message == null || message.getDeleted() == 1) return Result.error("404", "消息不存在");
            if (!message.getReceiverId().equals(userId)) return Result.error("403", "无权查看");
            return Result.success(message);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "全部标记已读")
    @PostMapping("/read-all")
    public Result<String> markAllAsRead() {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) return Result.error("401", "用户未认证");
            boolean ok = messageService.markAllAsRead(userId);
            return ok ? Result.success("标记成功") : Result.error("标记失败");
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "删除消息")
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteMessage(@PathVariable Long id) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) return Result.error("401", "用户未认证");
            boolean ok = messageService.deleteMessage(id, userId);
            return ok ? Result.success("删除成功") : Result.error("删除失败");
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }
}

