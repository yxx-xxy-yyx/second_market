package com.echoofmemories.project.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.ArrayList;

/**
 * OpenAI兼容API请求格式
 * 遵循OpenAI Chat Completions API标准
 * 
 * @author echo
 * @since 2025-01-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenAiCompatibleRequest {

    /**
     * 模型名称
     */
    private String model;

    /**
     * 消息列表
     */
    private List<Message> messages;

    /**
     * 最大token数
     */
    @JsonProperty("max_tokens")
    private Integer maxTokens;

    /**
     * 创造性参数(0.0-2.0)
     */
    private Double temperature;

    /**
     * 多样性惩罚参数(0.0-2.0)
     */
    @JsonProperty("frequency_penalty")
    private Double frequencyPenalty;

    /**
     * 重复惩罚参数(0.0-2.0)
     */
    @JsonProperty("presence_penalty")
    private Double presencePenalty;

    /**
     * 是否流式响应
     */
    private Boolean stream;

    /**
     * 消息对象
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        /**
         * 消息角色：system, user, assistant
         */
        private String role;

        /**
         * 消息内容（文本或多模态）
         */
        @JsonProperty("content")
        private Object content;

        public void setContentList(List<Object> contentList) {
            this.content = contentList;
        }
    }

    /**
     * 创建用户消息请求
     * 
     * @param model       模型名称
     * @param userMessage 用户消息内容
     * @param maxTokens   最大token数
     * @param temperature 创造性参数
     * @return 请求对象
     */
    public static OpenAiCompatibleRequest createUserRequest(String model, String userMessage,
            Integer maxTokens, Double temperature) {
        List<Message> messages = new ArrayList<>();
        messages.add(Message.builder()
                .role("user")
                .content(userMessage)
                .build());

        return OpenAiCompatibleRequest.builder()
                .model(model)
                .messages(messages)
                .maxTokens(maxTokens)
                .temperature(temperature)
                .stream(false)
                .build();
    }

    /**
     * 创建系统+用户消息请求
     * 
     * @param model         模型名称
     * @param systemMessage 系统消息（设定AI角色和规则）
     * @param userMessage   用户消息内容
     * @param maxTokens     最大token数
     * @param temperature   创造性参数
     * @return 请求对象
     */
    public static OpenAiCompatibleRequest createSystemUserRequest(String model, String systemMessage,
            String userMessage, Integer maxTokens,
            Double temperature) {
        List<Message> messages = new ArrayList<>();
        messages.add(Message.builder()
                .role("system")
                .content(systemMessage)
                .build());
        messages.add(Message.builder()
                .role("user")
                .content(userMessage)
                .build());

        return OpenAiCompatibleRequest.builder()
                .model(model)
                .messages(messages)
                .maxTokens(maxTokens)
                .temperature(temperature)
                .stream(false)
                .build();
    }

    public static OpenAiCompatibleRequest createSystemMessagesRequest(String model,
            String systemMessage,
            List<Message> conversation,
            Integer maxTokens,
            Double temperature) {
        List<Message> messages = new ArrayList<>();
        messages.add(Message.builder()
                .role("system")
                .content(systemMessage)
                .build());
        if (conversation != null) {
            messages.addAll(conversation);
        }

        return OpenAiCompatibleRequest.builder()
                .model(model)
                .messages(messages)
                .maxTokens(maxTokens)
                .temperature(temperature)
                .stream(false)
                .build();
    }
}
