package com.echoofmemories.project.service;

import com.echoofmemories.project.dto.AiChatRequest;
import com.echoofmemories.project.dto.AiRequest;
import com.echoofmemories.project.dto.AiResponse;
import com.echoofmemories.project.dto.ProductAnalysisDTO;
import java.util.List;

/**
 * AI服务接口
 * 提供AI生成相关功能
 * 
 * @author echo
 * @since 2025-01-27
 */
public interface AiService {

    /**
     * 通用AI内容生成
     * 
     * @param request AI生成请求
     * @return AI生成响应
     */
    AiResponse generateContent(AiRequest request);

    /**
     * 生成内容摘要
     * 
     * @param content 原始内容
     * @param userId  用户ID
     * @return AI生成响应
     */
    AiResponse generateSummary(String content, Long userId);

    /**
     * 生成内容标签
     * 
     * @param content 原始内容
     * @param userId  用户ID
     * @return AI生成响应
     */
    AiResponse generateTags(String content, Long userId);

    /**
     * 基于对话历史生成AI回复
     *
     * @param messages   对话消息列表
     * @param userId     用户ID
     * @param maxTokens  最大token数
     * @param temperature 创造性参数
     * @return AI生成响应
     */
    AiResponse chat(List<AiChatRequest.Message> messages, Long userId, Integer maxTokens, Double temperature);

    /**
     * 测试AI服务连接
     * 
     * @return 连接测试结果
     */
    boolean testConnection();

    /**
     * 检查AI服务是否可用
     * 
     * @return true-可用，false-不可用
     */
    boolean isAvailable();

    /**
     * 获取AI服务状态信息
     * 
     * @return 状态信息
     */
    String getServiceStatus();

    /**
     * 分析商品图片
     * 
     * @param imageUrls 图片URL列表
     * @param additionalInfo 附加信息
     * @param userId 用户ID
     * @return 商品分析结果
     */
    ProductAnalysisDTO analyzeProductImages(
            List<String> imageUrls,
            String additionalInfo,
            Long userId,
            String language);
}
