package com.echoofmemories.project.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * OpenAI兼容API响应格式
 * 遵循OpenAI Chat Completions API标准
 * 
 * @author echo
 * @since 2025-01-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenAiCompatibleResponse {

    /**
     * 响应ID
     */
    private String id;

    /**
     * 对象类型，通常为"chat.completion"
     */
    private String object;

    /**
     * 创建时间戳
     */
    private Long created;

    /**
     * 使用的模型名称
     */
    private String model;

    /**
     * 选择结果列表
     */
    private List<Choice> choices;

    /**
     * token使用情况
     */
    private Usage usage;

    /**
     * 选择结果
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Choice {
        /**
         * 选择索引
         */
        private Integer index;

        /**
         * 消息内容
         */
        private Message message;

        /**
         * 结束原因：stop, length, content_filter, null
         */
        @JsonProperty("finish_reason")
        private String finishReason;
    }

    /**
     * 消息对象
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        /**
         * 消息角色
         */
        private String role;

        /**
         * 消息内容
         */
        private String content;
    }

    /**
     * token使用情况
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Usage {
        /**
         * 提示词token数
         */
        @JsonProperty("prompt_tokens")
        private Integer promptTokens;

        /**
         * 生成内容token数
         */
        @JsonProperty("completion_tokens")
        private Integer completionTokens;

        /**
         * 总token数
         */
        @JsonProperty("total_tokens")
        private Integer totalTokens;
    }

    /**
     * 获取第一个选择的消息内容
     * 
     * @return 消息内容，如果没有选择则返回null
     */
    public String getFirstChoiceContent() {
        if (choices != null && !choices.isEmpty() && choices.get(0).getMessage() != null) {
            return choices.get(0).getMessage().getContent();
        }
        return null;
    }

    /**
     * 检查响应是否成功
     * 
     * @return true-成功，false-失败
     */
    public boolean isSuccessful() {
        return choices != null && !choices.isEmpty() &&
                choices.get(0).getMessage() != null &&
                choices.get(0).getMessage().getContent() != null;
    }

    /**
     * 转换为AiResponse.TokenUsage
     * 
     * @return TokenUsage对象
     */
    public AiResponse.TokenUsage toTokenUsage() {
        if (usage == null) {
            return null;
        }

        return AiResponse.TokenUsage.builder()
                .promptTokens(usage.getPromptTokens())
                .completionTokens(usage.getCompletionTokens())
                .totalTokens(usage.getTotalTokens())
                .build();
    }
}
