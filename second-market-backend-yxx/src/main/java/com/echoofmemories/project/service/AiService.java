package com.echoofmemories.project.service;

import com.echoofmemories.project.dto.*;
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

    /**
     * 智能托管服务
     * 
     * @param request 托管请求
     * @param userId 用户ID
     * @return 托管响应
     */
    AiIntelligentTrustResponse intelligentTrust(
            AiIntelligentTrustRequest request,
            Long userId);

    /**
     * 鉴定质检服务
     * 
     * @param request 鉴定请求
     * @param userId 用户ID
     * @return 鉴定响应
     */
    AiAuthenticationResponse authenticateProduct(
            AiAuthenticationRequest request,
            Long userId);

    /**
     * 市场行情参考
     * 
     * @param request 行情请求
     * @param userId 用户ID
     * @return 行情响应
     */
    AiMarketTrendResponse getMarketTrend(
            AiMarketTrendRequest request,
            Long userId);

    /**
     * 智能搜索
     * 
     * @param request 搜索请求
     * @param userId 用户ID
     * @return 搜索响应
     */
    AiSmartSearchResponse smartSearch(
            AiSmartSearchRequest request,
            Long userId);

    /**
     * 校园匹配
     * 
     * @param request 匹配请求
     * @param userId 用户ID
     * @return 匹配响应
     */
    AiCampusMatchResponse campusMatch(
            AiCampusMatchRequest request,
            Long userId);

    /**
     * AI纠纷仲裁
     * 
     * @param request 仲裁请求
     * @param userId 用户ID
     * @return 仲裁响应
     */
    AiDisputeResolutionResponse resolveDispute(
            AiDisputeResolutionRequest request,
            Long userId);

    /**
     * 校园专属服务
     * 
     * @param request 服务请求
     * @param userId 用户ID
     * @return 服务响应
     */
    AiCampusServiceResponse campusService(
            AiCampusServiceRequest request,
            Long userId);
}
