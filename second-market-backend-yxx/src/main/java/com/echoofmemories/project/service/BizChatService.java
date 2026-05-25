package com.echoofmemories.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.echoofmemories.project.entity.BizChat;

/**
 * 私聊消息 Service
 */
public interface BizChatService extends IService<BizChat> {
    java.util.List<com.echoofmemories.project.vo.ChatListVo> selectRecentChats(Long userId);
    Integer getTotalUnreadCount(Long userId);
}
