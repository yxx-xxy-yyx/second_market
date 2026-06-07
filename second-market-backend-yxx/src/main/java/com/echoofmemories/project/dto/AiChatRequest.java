package com.echoofmemories.project.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * AI聊天请求DTO
 *
 * @author echo
 * @since 2026-06-07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI聊天请求")
public class AiChatRequest {

    @NotEmpty(message = "消息列表不能为空")
    @Valid
    @Schema(description = "对话消息列表，按顺序传递", required = true)
    private List<Message> messages;

    @Schema(description = "最大token数，可选", example = "800")
    private Integer maxTokens;

    @Schema(description = "创造性参数，可选", example = "0.7")
    private Double temperature;

    @Schema(description = "用户ID（后端自动填充）", hidden = true)
    private Long userId;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "单条聊天消息")
    public static class Message {

        @NotBlank(message = "消息角色不能为空")
        @Schema(description = "消息角色，user or assistant", example = "user")
        private String role;

        @NotBlank(message = "消息内容不能为空")
        @Schema(description = "消息内容", example = "请帮我分析这件商品")
        private String content;
    }
}
