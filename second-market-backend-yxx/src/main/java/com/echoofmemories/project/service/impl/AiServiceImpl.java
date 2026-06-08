package com.echoofmemories.project.service.impl;

import com.echoofmemories.project.dto.*;
import com.echoofmemories.project.exception.CustomException;
import com.echoofmemories.project.service.AiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AI 服务门面（Facade Pattern）。
 * <p>
 * 本类只负责接口适配与调用路由，不承载任何具体业务实现：
 * <ul>
 *   <li>LLM API 调用 → {@link LlmClientService}</li>
 *   <li>图片预处理 + SSRF 防护 → {@link ImagePreprocessorService}</li>
 *   <li>用户级限流 → {@link RateLimiterService}</li>
 *   <li>商品鉴定 / 数据聚合 → {@link ProductAuthenticationService}</li>
 *   <li>市场行情 → {@link MarketTrendService}</li>
 *   <li>智能搜索 → {@link SmartSearchService}</li>
 *   <li>校园匹配 → {@link CampusMatchService}</li>
 *   <li>智能托管 → {@link IntelligentTrustService}</li>
 *   <li>纠纷仲裁 → {@link DisputeResolutionService}</li>
 *   <li>个性化推荐 → {@link RecommendationService}</li>
 *   <li>内容生成（摘要/标签/通用） → {@link AiContentService}</li>
 *   <li>校园专属服务 → {@link CampusServiceService}</li>
 * </ul>
 * </p>
 *
 * @author Echo of Memories
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    // ===== 基础设施层 =====
    private final LlmClientService llmClientService;
    private final ImagePreprocessorService imagePreprocessorService;
    private final RateLimiterService rateLimiterService;

    // ===== 业务层（每个方法一对一委派） =====
    private final ProductAuthenticationService productAuthenticationService;
    private final MarketTrendService marketTrendService;
    private final SmartSearchService smartSearchService;
    private final CampusMatchService campusMatchService;
    private final IntelligentTrustService intelligentTrustService;
    private final DisputeResolutionService disputeResolutionService;
    private final RecommendationService recommendationService;
    private final AiContentService aiContentService;
    private final AiCampusServiceService campusServiceService;

    // ============================================================
    // 文本生成类
    // ============================================================

    @Override
    public AiResponse generateContent(AiRequest request) {
        return aiContentService.generateContent(request);
    }

    @Override
    public AiResponse generateSummary(String content, Long userId) {
        return aiContentService.generateSummary(content, userId);
    }

    @Override
    public AiResponse generateTags(String content, Long userId) {
        return aiContentService.generateTags(content, userId);
    }

    // ============================================================
    // 健康检查 / 可用性
    // ============================================================

    @Override
    public boolean testConnection() {
        return aiContentService.testConnection();
    }

    @Override
    public boolean isAvailable() {
        return aiContentService.isAvailable();
    }

    @Override
    public String getServiceStatus() {
        return aiContentService.getServiceStatus();
    }

    // ============================================================
    // 多模态（视觉）
    // ============================================================

    @Override
    public ProductAnalysisDTO analyzeProductImages(
            List<String> imageUrls,
            String additionalInfo,
            Long userId,
            String language) {

        log.info("AI商品分析 - 用户ID={}, 图片数量={}", userId, imageUrls == null ? 0 : imageUrls.size());

        if (!llmClientService.isAvailable()) {
            throw new CustomException("AI服务当前不可用");
        }
        if (!rateLimiterService.checkRateLimit(userId)) {
            throw new CustomException("请求过于频繁，请稍后重试");
        }
        if (imageUrls == null || imageUrls.isEmpty()) {
            throw new CustomException("图片URL列表不能为空");
        }

        // 图片预处理：下载 / 压缩 / SSRF 防护 → 交给 ImagePreprocessorService
        List<String> processedImageUrls = imagePreprocessorService.optimizeImageUrls(imageUrls);
        log.info("图片预处理完成 - 实际使用 {} 张（原始 {} 张）", processedImageUrls.size(), imageUrls.size());

        // 视觉推理：交给 LlmClientService
        return llmClientService.analyzeImages(processedImageUrls, additionalInfo, language);
    }

    // ============================================================
    // 智能托管
    // ============================================================

    @Override
    public AiIntelligentTrustResponse intelligentTrust(
            AiIntelligentTrustRequest request,
            Long userId) {
        return intelligentTrustService.intelligentTrust(request, userId);
    }

    // ============================================================
    // 商品鉴定（规则 + 数据聚合）
    // ============================================================

    @Override
    public AiAuthenticationResponse authenticateProduct(
            AiAuthenticationRequest request,
            Long userId) {
        return productAuthenticationService.authenticateProduct(request, userId);
    }

    // ============================================================
    // 市场行情
    // ============================================================

    @Override
    public AiMarketTrendResponse getMarketTrend(
            AiMarketTrendRequest request,
            Long userId) {
        return marketTrendService.getMarketTrend(request, userId);
    }

    // ============================================================
    // 智能搜索
    // ============================================================

    @Override
    public AiSmartSearchResponse smartSearch(
            AiSmartSearchRequest request,
            Long userId) {
        return smartSearchService.smartSearch(request, userId);
    }

    // ============================================================
    // 校园匹配
    // ============================================================

    @Override
    public AiCampusMatchResponse campusMatch(
            AiCampusMatchRequest request,
            Long userId) {
        return campusMatchService.campusMatch(request, userId);
    }

    // ============================================================
    // 纠纷仲裁
    // ============================================================

    @Override
    public AiDisputeResolutionResponse resolveDispute(
            AiDisputeResolutionRequest request,
            Long userId) {
        return disputeResolutionService.resolveDispute(request, userId);
    }

    // ============================================================
    // 校园专属服务
    // ============================================================

    @Override
    public AiCampusServiceResponse campusService(
            AiCampusServiceRequest request,
            Long userId) {
        return campusServiceService.campusService(request, userId);
    }

    // ============================================================
    // 个性化推荐
    // ============================================================

    @Override
    public AiRecommendationResponse getRecommendations(
            AiRecommendationRequest request,
            Long userId) {
        return recommendationService.getRecommendations(request, userId);
    }
}
