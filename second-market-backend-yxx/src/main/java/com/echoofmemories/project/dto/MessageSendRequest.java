package com.echoofmemories.project.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 发送消息请求DTO
 * 
 * @author EchoOfMemories
 */
@Data
public class MessageSendRequest {
    
    private Long receiverId;
    
    private Integer type;
    
    @NotBlank(message = "标题不能为空")
    private String title;
    
    @NotBlank(message = "内容不能为空")
    private String content;
}

