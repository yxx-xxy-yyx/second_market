package com.echoofmemories.project.service.impl;

import com.echoofmemories.project.dto.AiAuthenticationRequest;
import com.echoofmemories.project.dto.AiAuthenticationResponse;
import com.echoofmemories.project.entity.Product;
import com.echoofmemories.project.entity.User;
import com.echoofmemories.project.mapper.ProductMapper;
import com.echoofmemories.project.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductAuthenticationService {

    private final ProductMapper productMapper;
    private final UserMapper userMapper;
    private final LlmClientService llmClient;

    public AiAuthenticationResponse authenticateProduct(AiAuthenticationRequest request, Long userId) {
        log.info("鉴定质检服务 - 用户ID: {}", userId);

        AiAuthenticationResponse response = new AiAuthenticationResponse();

        Long productId = request.getProductId();
        Product product = null;
        if (productId != null) {
            product = productMapper.selectById(productId);
        }

        if (product == null) {
            response.setIsAuthentic(false);
            response.setAuthenticityScore(0.3);
            response.setConditionGrade("C");
            response.setConditionScore(0.0);
            response.setConditionDetails(Collections.emptyList());
            response.setReportSummary("未查询到商品信息，无法完成鉴定");
            response.setRecommendations(Collections.singletonList("请确认商品ID是否正确"));
            response.setReportTime(LocalDateTime.now());
            response.setReportId("AUTH-" + System.currentTimeMillis());
            log.info("鉴定结果 - 未查询到商品, productId={}", productId);
            return response;
        }

        String images = product.getImages();
        int imageCount = 0;
        if (images != null && !images.isEmpty()) {
            String[] parts = images.split(",");
            for (String p : parts) {
                if (p != null && !p.trim().isEmpty()) imageCount++;
            }
        }

        String desc = product.getDescription();
        int descLen = desc == null ? 0 : desc.length();

        Integer aiAnalyzed = product.getAiAnalyzed();
        String aiSuggestions = product.getAiSuggestions();

        Integer condScore = product.getConditionScore();
        int condInt = condScore == null ? 0 : condScore;

        double authenticityScore = 0.35;
        if (imageCount > 2) authenticityScore += 0.15;
        if (descLen > 100) authenticityScore += 0.10;
        if (aiAnalyzed != null && aiAnalyzed == 1 && aiSuggestions != null && !aiSuggestions.isEmpty()) {
            authenticityScore += 0.20;
        }
        if (condInt >= 8) authenticityScore += 0.15;

        User seller = null;
        if (product.getUserId() != null) {
            seller = userMapper.selectById(product.getUserId());
        }
        if (seller != null && seller.getCreditScore() != null && seller.getCreditScore() >= 90) {
            authenticityScore += 0.15;
        }
        if (authenticityScore > 1.0) authenticityScore = 1.0;

        String grade;
        if (authenticityScore >= 0.85) grade = "A+";
        else if (authenticityScore >= 0.7) grade = "A";
        else if (authenticityScore >= 0.55) grade = "B";
        else grade = "C";

        boolean isAuthentic = authenticityScore >= 0.55;

        List<String> conditionDetails = new ArrayList<>();
        if (condInt > 0) {
            conditionDetails.add("成色评分：" + condInt + "/10");
        }
        if (product.getConditionDesc() != null && !product.getConditionDesc().isEmpty()) {
            conditionDetails.add("成色描述：" + product.getConditionDesc());
        }
        if (imageCount > 0) {
            conditionDetails.add("含实拍图片 " + imageCount + " 张");
        }
        if (descLen > 100) {
            conditionDetails.add("商品描述详细（" + descLen + "字）");
        }
        if (aiAnalyzed != null && aiAnalyzed == 1) {
            conditionDetails.add("已通过AI智能分析");
        }
        if (seller != null && seller.getCreditScore() != null) {
            conditionDetails.add("卖家信用分：" + seller.getCreditScore());
        }
        if (conditionDetails.isEmpty()) {
            conditionDetails.add("暂无详情信息");
        }

        BigDecimal avgPrice = null;
        BigDecimal lowestPrice = null;
        BigDecimal highestPrice = null;
        int categoryCount = 0;
        try {
            QueryWrapper<Product> qw = new QueryWrapper<>();
            qw.eq("deleted", 0);
            if (product.getCategory() != null) {
                qw.eq("category", product.getCategory());
            }
            qw.isNotNull("price");
            List<Product> sameCat = productMapper.selectList(qw);
            if (!sameCat.isEmpty()) {
                categoryCount = sameCat.size();
                BigDecimal total = BigDecimal.ZERO;
                BigDecimal min = sameCat.get(0).getPrice();
                BigDecimal max = sameCat.get(0).getPrice();
                for (Product p : sameCat) {
                    if (p.getPrice() == null) continue;
                    total = total.add(p.getPrice());
                    if (p.getPrice().compareTo(min) < 0) min = p.getPrice();
                    if (p.getPrice().compareTo(max) > 0) max = p.getPrice();
                }
                avgPrice = total.divide(new BigDecimal(sameCat.size()), 2, RoundingMode.HALF_UP);
                lowestPrice = min;
                highestPrice = max;
            }
        } catch (Exception e) {
            log.warn("同类商品统计失败: {}", e.getMessage());
        }

        List<String> recommendations = new ArrayList<>();
        if (imageCount <= 2) recommendations.add("建议添加更多细节图片（至少3张）");
        if (descLen <= 100) recommendations.add("建议补充更详细的商品描述（>100字）");
        if (condInt < 8) recommendations.add("成色一般，可考虑如实说明细节问题");
        if (aiAnalyzed == null || aiAnalyzed != 1) recommendations.add("建议使用AI商品分析增强可信度");
        if (product.getPrice() != null && avgPrice != null) {
            if (product.getPrice().compareTo(avgPrice.multiply(new BigDecimal("0.7"))) < 0) {
                recommendations.add("当前价格低于同类均价较多，请确认商品是否存在潜在问题");
            } else if (product.getPrice().compareTo(avgPrice.multiply(new BigDecimal("1.3"))) > 0) {
                recommendations.add("当前价格高于同类均价，建议优化定价促进成交");
            } else {
                recommendations.add("当前价格接近同类均价，定价合理");
            }
        }
        if (recommendations.isEmpty()) recommendations.add("商品信息完整，建议维持现状");

        String summary = "鉴定完成，真实度评分 " + String.format("%.2f", authenticityScore)
                + "，评级 " + grade + "。";
        if (avgPrice != null) {
            summary += " 同类商品参考均价 ¥" + avgPrice + "（共" + categoryCount + "件）。";
        }
        String priceSuggestion = null;
        if (product.getPrice() != null && avgPrice != null) {
            double diff = product.getPrice().doubleValue() - avgPrice.doubleValue();
            if (Math.abs(diff) < 1.0) {
                priceSuggestion = "与同类均价一致，建议保持当前价格";
            } else if (diff > 0) {
                priceSuggestion = "高于同类均价 ¥" + String.format("%.2f", diff) + "，建议适当下调";
            } else {
                priceSuggestion = "低于同类均价 ¥" + String.format("%.2f", -diff) + "，可考虑小幅上调";
            }
        }

        response.setIsAuthentic(isAuthentic);
        response.setAuthenticityScore(authenticityScore);
        response.setConditionGrade(grade);
        response.setConditionScore((double) condInt);
        response.setConditionDetails(conditionDetails);
        response.setReportSummary(summary);
        response.setRecommendations(recommendations);
        response.setReportTime(LocalDateTime.now());
        response.setReportId("AUTH-" + System.currentTimeMillis());

        log.info("鉴定结果 - productId={}, grade={}, score={}, avgPrice={}, 同品类数量={}",
                productId, grade, authenticityScore, avgPrice, categoryCount);

        return response;
    }
}
