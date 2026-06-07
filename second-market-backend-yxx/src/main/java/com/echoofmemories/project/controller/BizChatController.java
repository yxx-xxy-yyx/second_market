package com.echoofmemories.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.echoofmemories.project.common.Result;
import com.echoofmemories.project.entity.BizChat;
import com.echoofmemories.project.service.BizChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
// 必须使用 javax
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "私聊管理")
@RestController
@RequestMapping("/chat")
public class BizChatController {

    private final BizChatService bizChatService;

    public BizChatController(BizChatService bizChatService) {
        this.bizChatService = bizChatService;
    }

    // 使用 RequestContextHolder 获取 request，适配 Spring Boot 2.7.2
    private Long getCurrentUserId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return null;
        HttpServletRequest request = attributes.getRequest();

        // 这里的 "userId" 必须和拦截器中 setAttribute 的 key 完全一致
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj == null) return null;
        return Long.valueOf(userIdObj.toString());
    }

    @Operation(summary = "发送消息")
    @PostMapping("/send")
    public Result<Boolean> send(@RequestBody BizChat chat) {
        Long userId = getCurrentUserId();
        if (userId == null) return Result.error("未登录");

        chat.setSenderId(userId);
        chat.setCreateTime(LocalDateTime.now());
        chat.setIsRead(false);
        return Result.success(bizChatService.save(chat));
    }

    @Operation(summary = "获取聊天记录")
    @GetMapping("/history/{targetUserId}")
    public Result<List<BizChat>> history(@PathVariable Long targetUserId) {
        Long userId = getCurrentUserId();
        if (userId == null) return Result.error("未登录");

        LambdaQueryWrapper<BizChat> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w
                        .eq(BizChat::getSenderId, userId).eq(BizChat::getReceiverId, targetUserId)
                        .or()
                        .eq(BizChat::getSenderId, targetUserId).eq(BizChat::getReceiverId, userId))
                .orderByAsc(BizChat::getCreateTime);

        return Result.success(bizChatService.list(wrapper));
    }

    @Operation(summary = "获取最近聊天列表")
    @GetMapping("/list")
    public Result<List<com.echoofmemories.project.vo.ChatListVo>> list() {
        Long userId = getCurrentUserId();
        if (userId == null) return Result.error("未登录");
        return Result.success(bizChatService.selectRecentChats(userId));
    }

    @Operation(summary = "获取未读消息总数")
    @GetMapping("/unread/count")
    public Result<Integer> unreadCount() {
        Long userId = getCurrentUserId();
        if (userId == null) return Result.success(0);
        return Result.success(bizChatService.getTotalUnreadCount(userId));
    }

    @Operation(summary = "标记已读")
    @PostMapping("/read/{senderId}")
    public Result<Boolean> read(@PathVariable Long senderId) {
        Long userId = getCurrentUserId();
        if (userId == null) return Result.error("未登录");

        boolean update = bizChatService.lambdaUpdate()
                .set(BizChat::getIsRead, true)
                .eq(BizChat::getSenderId, senderId)
                .eq(BizChat::getReceiverId, userId)
                .eq(BizChat::getIsRead, false)
                .update();

        return Result.success(update);
    }

//    @Autowired
//    private com.echoofmemories.project.service.SysUserService sysUserService;
//
//    @Operation(summary = "管理员分页获取聊天列表")
//    @GetMapping("/admin/page")
//    public Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page<BizChat>> adminPage(
//            @RequestParam(defaultValue = "1") Integer page,
//            @RequestParam(defaultValue = "10") Integer size,
//            @RequestParam(required = false) String keyword) {
//
//        com.baomidou.mybatisplus.extension.plugins.pagination.Page<BizChat> chatPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(
//                page, size);
//        LambdaQueryWrapper<BizChat> wrapper = new LambdaQueryWrapper<>();
//
//        if (cn.hutool.core.util.StrUtil.isNotBlank(keyword)) {
//            wrapper.like(BizChat::getContent, keyword);
//        }
//
//        wrapper.orderByDesc(BizChat::getCreateTime);
//
//        bizChatService.page(chatPage, wrapper);
//
//        // Populate details (Sender, Receiver)
//        chatPage.getRecords().forEach(chat -> {
//            // Sender info
//            com.echoofmemories.project.entity.SysUser sender = sysUserService.getById(chat.getSenderId());
//            if (sender != null) {
//                sender.setPassword(null);
//                chat.setSender(sender);
//            }
//
//            // Receiver info
//            com.echoofmemories.project.entity.SysUser receiver = sysUserService.getById(chat.getReceiverId());
//            if (receiver != null) {
//                receiver.setPassword(null);
//                chat.setReceiver(receiver);
//            }
//        });
//
//        return Result.success(chatPage);
//    }

    @Operation(summary = "管理员删除聊天记录")
    @DeleteMapping("/admin/{id}")
    public Result<Boolean> adminRemove(@PathVariable Long id) {
        return Result.success(bizChatService.removeById(id));
    }
}