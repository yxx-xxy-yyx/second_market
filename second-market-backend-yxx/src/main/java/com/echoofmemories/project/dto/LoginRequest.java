package com.echoofmemories.project.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 登录请求DTO
 * 
 * @author Echo of Memories
 */
@Data
public class LoginRequest {

    /**
     * UID（兼容 username）
     */
    @NotBlank(message = "UID不能为空")
    @JsonProperty("uid")
    @JsonAlias({"username"})
    private String uid;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}
