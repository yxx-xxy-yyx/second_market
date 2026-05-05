package com.echoofmemories.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.echoofmemories.project.dto.MessageSendRequest;
import com.echoofmemories.project.entity.Message;

/**
 * 消息服务接口
 * 
 * @author EchoOfMemories
 */
public interface MessageService extends IService<Message> {
    
    boolean sendMessage(MessageSendRequest request);
    
    boolean sendMessageToAll(MessageSendRequest request);
    
    Page<Message> getMyMessagePage(Integer pageNum, Integer pageSize, Long userId, Integer isRead);
    
    boolean markAsRead(Long messageId, Long userId);
    
    Long getUnreadCount(Long userId);
}

