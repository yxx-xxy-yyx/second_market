package com.echoofmemories.project.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * 更新头像请求DTO
 * 
 * @author Echo of Memories
 */
@Data
public class UpdateAvatarRequest {

    /**
     * 头像URL
     */
    @NotBlank(message = "头像URL不能为空")
    private String avatarUrl;
}
