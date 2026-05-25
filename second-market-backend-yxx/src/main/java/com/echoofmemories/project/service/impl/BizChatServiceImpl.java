package com.echoofmemories.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.echoofmemories.project.entity.BizChat;
import com.echoofmemories.project.mapper.BizChatMapper;
import com.echoofmemories.project.service.BizChatService;
import org.springframework.stereotype.Service;

/**
 * 私聊消息 Service实现类
 */
@Service
public class BizChatServiceImpl extends ServiceImpl<BizChatMapper, BizChat> implements BizChatService {

    @Override
    public java.util.List<com.echoofmemories.project.vo.ChatListVo> selectRecentChats(Long userId) {
        return baseMapper.selectRecentChats(userId);
    }

    @Override
    public Integer getTotalUnreadCount(Long userId) {
        return baseMapper.selectTotalUnreadCount(userId);
    }
}
