package com.echoofmemories.project.dto;

import lombok.Data;

/**
 * 活跃用户DTO
 * 
 * @author EchoOfMemories
 */
@Data
public class ActiveUserDTO {
    
    private Long userId;
    
    private String nickname;
    
    private String avatar;
    
    private Long productCount;
    
    private Long orderCount;
    
    private Long totalActivity;
}

