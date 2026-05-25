package com.echoofmemories.project.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户详细信息DTO
 * 
 * @author EchoOfMemories
 */
@Data
public class UserDetailDTO {
    
    private Long id;
    
    private String username;
    
    private String nickname;
    
    private String email;
    
    private String phone;
    
    private String address;
    
    private String avatar;
    
    private String role;
    
    private Integer status;
    
    private Integer creditScore;
    
    private Long productCount;
    
    private Long orderCount;
    
    private LocalDateTime createTime;
    private Long schoolId;
    private String schoolName;
}

