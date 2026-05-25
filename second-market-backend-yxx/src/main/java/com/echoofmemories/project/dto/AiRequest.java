package com.echoofmemories.project.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * AI生成请求DTO
 * 
 * @author echo
 * @since 2025-01-27
 */
@Data
@Schema(description = "AI生成请求")
public class AiRequest {

    /**
     * 用户提示词
     */
    @NotBlank(message = "提示词不能为空")
    @Schema(description = "用户提示词", example = "请帮我写一篇关于Spring Boot的技术文章")
    private String prompt;

    /**
     * 生成类型
     */
    @NotNull(message = "生成类型不能为空")
    @Schema(description = "生成类型", example = "POST_CONTENT")
    private AiGenerateType type;

    /**
     * 最大token数（可选，覆盖配置文件设置）
     */
    @Min(value = 1, message = "maxTokens必须大于0")
    @Max(value = 4000, message = "maxTokens不能超过4000")
    @Schema(description = "最大token数", example = "1000")
    private Integer maxTokens;

    /**
     * 创造性参数（可选，覆盖配置文件设置）
     */
    @Min(value = 0, message = "temperature不能小于0")
    @Max(value = 2, message = "temperature不能大于2")
    @Schema(description = "创造性参数(0.0-2.0)", example = "0.7")
    private Double temperature;

    /**
     * 上下文内容（用于摘要和标签生成）
     */
    @Schema(description = "上下文内容", example = "这是一段需要生成摘要的文章内容...")
    private String context;

    /**
     * 用户ID（由后端自动填充，前端不需要传递）
     */
    @Schema(description = "用户ID（后端自动填充）", hidden = true)
    private Long userId;

    /**
     * AI生成类型枚举
     */
    public enum AiGenerateType {
        /**
         * 帖子内容生成
         */
        POST_CONTENT("帖子内容"),

        /**
         * 帖子摘要生成
         */
        POST_SUMMARY("帖子摘要"),

        /**
         * 帖子标签生成
         */
        POST_TAGS("帖子标签"),

        /**
         * 通用内容生成
         */
        GENERAL_CONTENT("通用内容");

        private final String description;

        AiGenerateType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
