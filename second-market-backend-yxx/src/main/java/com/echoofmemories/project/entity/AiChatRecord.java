package com.echoofmemories.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI 聊天记录
 */
@Data
@TableName("ai_chat_record")
public class AiChatRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 角色 user / assistant
     */
    private String role;

    /**
     * 聊天内容
     */
    private String content;

    /**
     * 记录时间
     */
    private LocalDateTime createTime;
}
