package com.echoofmemories.project.dto;

import lombok.Data;

/**
 * 公告请求DTO
 * 
 * @author EchoOfMemories
 */
@Data
public class NoticeRequest {
    
    private Long id;
    
    private Integer type;
    
    private String title;
    
    private String content;
    
    private Integer status;
}

