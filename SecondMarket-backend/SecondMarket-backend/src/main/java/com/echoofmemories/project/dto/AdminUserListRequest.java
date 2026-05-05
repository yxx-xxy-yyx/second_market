package com.echoofmemories.project.dto;

import lombok.Data;

/**
 * 管理员查询用户列表请求DTO
 * 
 * @author EchoOfMemories
 */
@Data
public class AdminUserListRequest {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    private String keyword;

    private String username;

    private String nickname;

    private String phone;

    private String status;

    private Long schoolId;

    private String language;
}
