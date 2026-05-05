package com.echoofmemories.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.echoofmemories.project.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 消息数据访问层
 * 
 * @author EchoOfMemories
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {
    
    Page<Message> selectMyMessagePage(Page<Message> page, 
                                     @Param("receiverId") Long receiverId,
                                     @Param("isRead") Integer isRead);
    
    Long selectUnreadCount(@Param("receiverId") Long receiverId);
}

