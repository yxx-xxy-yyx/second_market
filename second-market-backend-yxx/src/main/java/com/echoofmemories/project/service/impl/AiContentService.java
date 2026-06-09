package com.echoofmemories.project.service.impl;

import com.echoofmemories.project.dto.AiRequest;
import com.echoofmemories.project.dto.AiResponse;
import com.echoofmemories.project.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * AI内容生成服务
 * 提供通用文本生成、摘要、标签及健康检查
 *
 * @author echo
 * @since 2025-01-27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiContentService {

    private final LlmClientService llmClient;
    private final RateLimiterService rateLimiter;

    public AiResponse generateContent(AiRequest request) {
        log.info("开始AI内容生成，用户ID：{}，类型：{}", request.getUserId(), request.getType());

        // 检查AI服务是否可用
        if (!llmClient.isAvailable()) {
            throw new CustomException("AI服务当前不可用");
        }

        // 限流检查
        if (!rateLimiter.checkRateLimit(request.getUserId())) {
            throw new CustomException("请求过于频繁，请稍后重试");
        }

        // 调用LLM客户端
        String typeName = request.getType() != null ? request.getType().name() : AiRequest.AiGenerateType.GENERAL_CONTENT.name();
        return llmClient.generateContent(typeName, request.getPrompt(), request.getContext());
    }

    public AiResponse generateSummary(String content, Long userId) {
        if (!StringUtils.hasText(content)) {
            throw new CustomException("内容不能为空");
        }

        AiRequest request = new AiRequest();
        request.setPrompt("请为以下内容生成一个简洁的摘要");
        request.setContext(content);
        request.setType(AiRequest.AiGenerateType.POST_SUMMARY);
        request.setUserId(userId);

        return generateContent(request);
    }

    public AiResponse generateTags(String content, Long userId) {
        if (!StringUtils.hasText(content)) {
            throw new CustomException("内容不能为空");
        }

        AiRequest request = new AiRequest();
        request.setPrompt("请为以下内容生成3-5个相关标签");
        request.setContext(content);
        request.setType(AiRequest.AiGenerateType.POST_TAGS);
        request.setUserId(userId);

        return generateContent(request);
    }

    public boolean testConnection() {
        try {
            AiRequest testRequest = new AiRequest();
            testRequest.setPrompt("Hello");
            testRequest.setType(AiRequest.AiGenerateType.GENERAL_CONTENT);
            testRequest.setMaxTokens(10);
            testRequest.setUserId(0L); // 测试请求使用特殊用户ID

            AiResponse response = generateContent(testRequest);
            return response != null && StringUtils.hasText(response.getContent());
        } catch (Exception e) {
            log.warn("AI服务连接测试失败：{}", e.getMessage());
            return false;
        }
    }

    public boolean isAvailable() {
        return llmClient.isAvailable();
    }

    public String getServiceStatus() {
        return llmClient.getServiceStatus();
    }
}
