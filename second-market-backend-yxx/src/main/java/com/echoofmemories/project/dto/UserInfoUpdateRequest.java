package com.echoofmemories.project.dto;

import lombok.Data;

import javax.validation.constraints.Pattern;

/**
 * 用户信息更新请求DTO
 * 
 * @author EchoOfMemories
 */
@Data
public class UserInfoUpdateRequest {
    
    private String nickname;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    private String address;
    
    private String avatar;
    
    private String bio;
}

