package com.echoofmemories.project.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * AI生成响应DTO
 * 
 * @author echo
 * @since 2025-01-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI生成响应")
public class AiResponse {

    /**
     * 生成的内容
     */
    @Schema(description = "生成的内容")
    private String content;

    /**
     * token使用情况
     */
    @Schema(description = "token使用情况")
    private TokenUsage usage;

    /**
     * 使用的模型名称
     */
    @Schema(description = "使用的模型名称", example = "gpt-3.5-turbo")
    private String model;

    /**
     * 生成类型
     */
    @Schema(description = "生成类型")
    private AiRequest.AiGenerateType type;

    /**
     * 生成时间（毫秒）
     */
    @Schema(description = "生成耗时（毫秒）", example = "1500")
    private Long duration;

    /**
     * Token使用情况
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Token使用情况")
    public static class TokenUsage {
        /**
         * 提示词token数
         */
        @Schema(description = "提示词token数", example = "100")
        private Integer promptTokens;

        /**
         * 生成内容token数
         */
        @Schema(description = "生成内容token数", example = "150")
        private Integer completionTokens;

        /**
         * 总token数
         */
        @Schema(description = "总token数", example = "250")
        private Integer totalTokens;
    }

    /**
     * 创建成功响应
     * 
     * @param content  生成内容
     * @param model    模型名称
     * @param type     生成类型
     * @param duration 耗时
     * @return AI响应
     */
    public static AiResponse success(String content, String model,
            AiRequest.AiGenerateType type, Long duration) {
        return AiResponse.builder()
                .content(content)
                .model(model)
                .type(type)
                .duration(duration)
                .build();
    }

    /**
     * 创建成功响应（包含token使用情况）
     * 
     * @param content  生成内容
     * @param model    模型名称
     * @param type     生成类型
     * @param duration 耗时
     * @param usage    token使用情况
     * @return AI响应
     */
    public static AiResponse success(String content, String model,
            AiRequest.AiGenerateType type, Long duration,
            TokenUsage usage) {
        return AiResponse.builder()
                .content(content)
                .model(model)
                .type(type)
                .duration(duration)
                .usage(usage)
                .build();
    }
}
