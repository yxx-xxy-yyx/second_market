package com.echoofmemories.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.echoofmemories.project.dto.MessageSendRequest;
import com.echoofmemories.project.entity.Message;
import com.echoofmemories.project.entity.User;
import com.echoofmemories.project.exception.CustomException;
import com.echoofmemories.project.mapper.MessageMapper;
import com.echoofmemories.project.service.MessageService;
import com.echoofmemories.project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 消息服务实现类
 * 
 * @author EchoOfMemories
 */
@Service
@RequiredArgsConstructor
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    private final MessageMapper messageMapper;
    private final UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean sendMessage(MessageSendRequest request) {
        if (request.getReceiverId() == null) {
            throw new CustomException("接收者ID不能为空");
        }

        User receiver = userService.getById(request.getReceiverId());
        if (receiver == null) {
            throw new CustomException("接收者不存在");
        }

        Message message = new Message();
        message.setReceiverId(request.getReceiverId());
        message.setType(request.getType() != null ? request.getType() : 0);
        message.setTitle(request.getTitle());
        message.setContent(request.getContent());
        message.setIsRead(0);
        message.setDeleted(0);

        return this.save(message);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean sendMessageToAll(MessageSendRequest request) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(User::getId);
        List<User> allUsers = userService.list(queryWrapper);

        if (allUsers.isEmpty()) {
            throw new CustomException("没有用户可以发送消息");
        }

        List<Message> messages = new ArrayList<>();
        for (User user : allUsers) {
            Message message = new Message();
            message.setReceiverId(user.getId());
            message.setType(request.getType() != null ? request.getType() : 0);
            message.setTitle(request.getTitle());
            message.setContent(request.getContent());
            message.setIsRead(0);
            message.setDeleted(0);
            messages.add(message);
        }

        return this.saveBatch(messages);
    }

    @Override
    public Page<Message> getMyMessagePage(Integer pageNum, Integer pageSize, Long userId, Integer isRead) {
        Page<Message> page = new Page<>(pageNum, pageSize);
        return messageMapper.selectMyMessagePage(page, userId, isRead);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsRead(Long messageId, Long userId) {
        Message message = this.getById(messageId);
        if (message == null) {
            throw new CustomException("消息不存在");
        }

        if (!message.getReceiverId().equals(userId)) {
            throw new CustomException("无权操作他人消息");
        }

        if (message.getIsRead() == 1) {
            return true;
        }

        message.setIsRead(1);
        message.setReadTime(LocalDateTime.now());
        return this.updateById(message);
    }

    @Override
    public Long getUnreadCount(Long userId) {
        return messageMapper.selectUnreadCount(userId);
    }
}

