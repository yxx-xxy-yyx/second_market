package com.echoofmemories.project.service.impl;

import com.echoofmemories.project.dto.AiRecommendationRequest;
import com.echoofmemories.project.dto.AiRecommendationResponse;
import com.echoofmemories.project.dto.AiRequest;
import com.echoofmemories.project.dto.AiResponse;
import com.echoofmemories.project.entity.BrowseHistory;
import com.echoofmemories.project.entity.Product;
import com.echoofmemories.project.entity.SearchHistory;
import com.echoofmemories.project.service.BrowseHistoryService;
import com.echoofmemories.project.service.ProductService;
import com.echoofmemories.project.service.SearchHistoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 智能推荐服务
 * 基于浏览/搜索历史的个性化推荐
 *
 * @author echo
 * @since 2025-01-27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final BrowseHistoryService browseHistoryService;
    private final SearchHistoryService searchHistoryService;
    private final ProductService productService;
    private final LlmClientService llmClient;

    public AiRecommendationResponse getRecommendations(AiRecommendationRequest request, Long userId) {

        long startTime = System.currentTimeMillis();
        AiRecommendationResponse response = new AiRecommendationResponse();

        try {
            log.info("AI推荐 - 用户ID: {}, 类型: {}", userId, request.getType());

            // 获取用户数据
            List<BrowseHistory> browseHistories = getRecentBrowseHistory(userId, 30);
            List<SearchHistory> searchHistories = searchHistoryService.getVisibleHistory(userId, 20);

            // 提取用户兴趣
            Set<String> userCategories = new HashSet<>();
            Set<String> userKeywords = new HashSet<>();
            Set<Long> viewedProductIds = new HashSet<>();

            for (BrowseHistory bh : browseHistories) {
                if (bh.getProduct() != null) {
                    userCategories.add(bh.getProduct().getCategory());
                    viewedProductIds.add(bh.getProductId());
                }
            }

            for (SearchHistory sh : searchHistories) {
                userKeywords.add(sh.getKeyword());
            }

            // 获取候选商品
            List<Product> candidates = getCandidateProducts(userId, userCategories, viewedProductIds, request.getLimit() + 20);

            // 对商品进行评分和排序
            List<AiRecommendationResponse.ProductWithReason> scoredProducts =
                    scoreAndRankProducts(candidates, userCategories, userKeywords, browseHistories, request);

            // 限制数量
            if (scoredProducts.size() > request.getLimit()) {
                scoredProducts = scoredProducts.subList(0, request.getLimit());
            }

            // 构建响应
            response.setSuccess(true);
            response.setProducts(scoredProducts);
            response.setInterestTags(new ArrayList<>(userCategories));
            response.setExplanation(generateExplanation(userCategories, userKeywords));
            response.setResponseTime(System.currentTimeMillis() - startTime);

            // 如果AI可用，生成AI推理说明
            if (llmClient.isAvailable()) {
                try {
                    response.setAiReasoning(generateAiReasoning(userCategories, userKeywords, scoredProducts));
                } catch (Exception e) {
                    log.warn("AI推理生成失败，使用默认说明", e);
                    response.setAiReasoning("基于您的浏览和搜索历史进行个性化推荐");
                }
            } else {
                response.setAiReasoning("基于您的浏览和搜索历史进行个性化推荐");
            }

            log.info("AI推荐完成 - 用户ID: {}, 推荐数量: {}, 耗时: {}ms",
                    userId, scoredProducts.size(), System.currentTimeMillis() - startTime);

        } catch (Exception e) {
            log.error("AI推荐失败 - 用户ID: {}", userId, e);
            response.setSuccess(false);
            response.setExplanation("推荐服务暂时不可用，请稍后重试");
            response.setResponseTime(System.currentTimeMillis() - startTime);
        }

        return response;
    }

    private List<BrowseHistory> getRecentBrowseHistory(Long userId, int limit) {
        LambdaQueryWrapper<BrowseHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BrowseHistory::getUserId, userId)
                .orderByDesc(BrowseHistory::getCreateTime)
                .last("LIMIT " + limit);
        return browseHistoryService.list(wrapper);
    }

    private List<Product> getCandidateProducts(
            Long userId,
            Set<String> categories,
            Set<Long> excludeIds,
            int limit) {

        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1)
                .eq(Product::getDeleted, 0);

        if (!categories.isEmpty()) {
            wrapper.in(Product::getCategory, categories);
        }

        if (!excludeIds.isEmpty()) {
            wrapper.notIn(Product::getId, excludeIds);
        }

        if (userId != null) {
            wrapper.ne(Product::getUserId, userId);
        }

        wrapper.orderByDesc(Product::getViewCount)
                .orderByDesc(Product::getFavoriteCount)
                .last("LIMIT " + limit);

        return productService.list(wrapper);
    }

    private List<AiRecommendationResponse.ProductWithReason> scoreAndRankProducts(
            List<Product> products,
            Set<String> userCategories,
            Set<String> userKeywords,
            List<BrowseHistory> browseHistories,
            AiRecommendationRequest request) {

        List<AiRecommendationResponse.ProductWithReason> result = new ArrayList<>();

        for (Product product : products) {
            AiRecommendationResponse.ProductWithReason item =
                    new AiRecommendationResponse.ProductWithReason();
            item.setProduct(product);
            item.setId(product.getId());
            item.setTitle(product.getTitle());
            item.setDescription(product.getDescription());
            item.setCategory(product.getCategory());
            item.setPrice(product.getPrice());
            item.setImages(product.getImages());
            item.setViewCount(product.getViewCount());

            double score = 0.0;
            String reasonType = "hot";
            String reason = "热门推荐";

            // 类别匹配
            if (userCategories.contains(product.getCategory())) {
                score += 30.0;
                reasonType = "browse_history";
                reason = "根据您浏览的" + product.getCategory() + "商品推荐";
            }

            // 关键词匹配
            for (String keyword : userKeywords) {
                if ((product.getTitle() != null && product.getTitle().contains(keyword)) ||
                        (product.getDescription() != null && product.getDescription().contains(keyword))) {
                    score += 25.0;
                    reasonType = "search";
                    reason = "与您搜索的\"" + keyword + "\"相关";
                    break;
                }
            }

            // 热门度评分
            int viewCount = product.getViewCount() != null ? product.getViewCount() : 0;
            int favoriteCount = product.getFavoriteCount() != null ? product.getFavoriteCount() : 0;
            score += Math.min(viewCount * 0.1, 20.0);
            score += Math.min(favoriteCount * 0.5, 15.0);

            // 成新度加分
            int conditionScore = product.getConditionScore() != null ? product.getConditionScore() : 5;
            score += conditionScore * 1.0;

            // 随机因素（增加多样性）
            score += Math.random() * 10.0;

            item.setMatchScore(Math.min(score, 100.0));
            item.setReasonType(reasonType);
            item.setReason(reason);

            result.add(item);
        }

        // 按分数排序
        result.sort((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()));

        return result;
    }

    private String generateExplanation(Set<String> categories, Set<String> keywords) {
        StringBuilder sb = new StringBuilder();
        sb.append("为您推荐 ");

        if (!categories.isEmpty()) {
            sb.append(String.join("、", categories));
            sb.append(" 等类别的商品");
        }

        if (!keywords.isEmpty()) {
            sb.append("，包含 ");
            sb.append(String.join("、", keywords.stream().limit(3).collect(Collectors.toList())));
            sb.append(" 等关键词");
        }

        return sb.toString();
    }

    private String generateAiReasoning(
            Set<String> categories,
            Set<String> keywords,
            List<AiRecommendationResponse.ProductWithReason> products) {

        if (!llmClient.isAvailable()) {
            return "基于您的浏览和搜索历史进行个性化推荐";
        }

        try {
            StringBuilder prompt = new StringBuilder();
            prompt.append("请用中文生成一段简短的推荐说明（不超过100字）。\n");
            prompt.append("用户兴趣类别: ").append(String.join("、", categories)).append("\n");
            prompt.append("用户搜索关键词: ").append(String.join("、", keywords)).append("\n");
            prompt.append("请说明是基于用户的浏览和搜索历史进行推荐。");

            AiRequest aiRequest = new AiRequest();
            aiRequest.setPrompt(prompt.toString());
            aiRequest.setType(AiRequest.AiGenerateType.GENERAL_CONTENT);
            aiRequest.setMaxTokens(150);

            AiResponse aiResponse = llmClient.generateContent(
                    aiRequest.getType().name(),
                    aiRequest.getPrompt(),
                    aiRequest.getContext());
            return aiResponse.getContent();

        } catch (Exception e) {
            log.warn("AI推理说明生成失败", e);
            return "基于您的浏览和搜索历史进行个性化推荐";
        }
    }
}
