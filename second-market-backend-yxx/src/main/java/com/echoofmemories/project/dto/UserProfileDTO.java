package com.echoofmemories.project.dto;

import lombok.Data;

/**
 * 可信用户身份信息 DTO
 * 字段均由后端 token 解析 + 数据库查询组装，不可由前端伪造
 *
 * @author EchoOfMemories
 */
@Data
public class UserProfileDTO {

    private Long id;

    private String username;

    private String nickname;

    private String email;

    private String avatar;

    /**
     * 角色字段（由后端 token 解析后注入，不可伪造）
     */
    private String role;

    private Long schoolId;

    private Integer creditScore;

    private Integer status;
}
