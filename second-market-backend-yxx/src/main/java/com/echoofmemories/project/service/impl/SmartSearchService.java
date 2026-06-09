package com.echoofmemories.project.service.impl;

import com.echoofmemories.project.dto.AiSmartSearchRequest;
import com.echoofmemories.project.dto.AiSmartSearchResponse;
import com.echoofmemories.project.entity.Product;
import com.echoofmemories.project.entity.User;
import com.echoofmemories.project.mapper.ProductMapper;
import com.echoofmemories.project.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmartSearchService {

    private final ProductMapper productMapper;
    private final UserMapper userMapper;

    public AiSmartSearchResponse smartSearch(AiSmartSearchRequest request, Long userId) {
        log.info("智能搜索 - 用户ID: {}, 查询: {}", userId, request.getQuery());

        AiSmartSearchResponse response = new AiSmartSearchResponse();

        String keyword = request.getQuery();
        String category = request.getCategory();
        Long userSchoolId = null;
        if (userId != null) {
            User u = userMapper.selectById(userId);
            if (u != null) userSchoolId = u.getSchoolId();
        }

        List<Product> hits = new ArrayList<>();
        try {
            QueryWrapper<Product> qw = new QueryWrapper<>();
            qw.eq("deleted", 0);
            if (category != null && !category.isEmpty()) {
                qw.eq("category", category);
            }
            if (keyword != null && !keyword.trim().isEmpty()) {
                String kw = "%" + keyword.trim().replace("%", "\\%") + "%";
                qw.and(w -> w.like("title", kw).or().like("description", kw));
            }
            if (request.getMinPrice() != null) {
                qw.ge("price", BigDecimal.valueOf(request.getMinPrice()));
            }
            if (request.getMaxPrice() != null) {
                qw.le("price", BigDecimal.valueOf(request.getMaxPrice()));
            }
            if (request.getConditionMin() != null) {
                qw.ge("condition_score", request.getConditionMin());
            }
            if (request.getConditionMax() != null) {
                qw.le("condition_score", request.getConditionMax());
            }
            qw.orderByDesc("view_count");
            qw.last("LIMIT 200");
            hits = productMapper.selectList(qw);
        } catch (Exception e) {
            log.warn("智能搜索失败: {}", e.getMessage());
        }

        List<Map<String, Object>> products = new ArrayList<>();
        for (Product p : hits) {
            double titleMatch = 0.0;
            double descMatch = 0.0;
            if (keyword != null && !keyword.trim().isEmpty()) {
                String k = keyword.trim().toLowerCase();
                if (p.getTitle() != null && p.getTitle().toLowerCase().contains(k)) {
                    titleMatch = 0.5;
                }
                if (p.getDescription() != null && p.getDescription().toLowerCase().contains(k)) {
                    descMatch = 0.3;
                }
            }
            double schoolBonus = 0.0;
            if (Boolean.TRUE.equals(request.getSchoolPriority())
                    && userSchoolId != null && p.getSchoolId() != null
                    && userSchoolId.equals(p.getSchoolId())) {
                schoolBonus = 0.3;
            }
            double matchScore = Math.min(1.0, titleMatch + descMatch + schoolBonus + 0.2);

            Map<String, Object> m = new HashMap<>();
            m.put("id", p.getId());
            m.put("title", p.getTitle());
            m.put("price", p.getPrice() == null ? 0 : p.getPrice().doubleValue());
            m.put("conditionScore", p.getConditionScore() == null ? 0 : p.getConditionScore());
            m.put("schoolId", p.getSchoolId());
            m.put("matchScore", matchScore);
            m.put("category", p.getCategory());
            products.add(m);
        }

        products.sort((a, b) -> Double.compare((Double) b.get("matchScore"), (Double) a.get("matchScore")));

        int pageSize = request.getPageSize() == null || request.getPageSize() <= 0 ? 20 : request.getPageSize();
        int pageNum = request.getPageNum() == null || request.getPageNum() <= 0 ? 1 : request.getPageNum();
        int totalCount = products.size();
        int from = Math.min(totalCount, (pageNum - 1) * pageSize);
        int to = Math.min(totalCount, from + pageSize);
        List<Map<String, Object>> pageList =
                from >= to ? new ArrayList<>() : new ArrayList<>(products.subList(from, to));

        Set<String> suggestedTags = new LinkedHashSet<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            String k = keyword.trim();
            suggestedTags.add(k);
            if (k.length() >= 2) {
                suggestedTags.add(k.substring(0, Math.min(4, k.length())));
            }
            for (String part : k.split("[,\\s，、]+")) {
                if (!part.isEmpty()) suggestedTags.add(part);
            }
        }
        if (category != null && !category.isEmpty()) suggestedTags.add(category);

        response.setInterpretedQuery(keyword == null ? "" : keyword);
        response.setSuggestedTags(new ArrayList<>(suggestedTags));
        response.setSearchTip(totalCount > 0 ? "共找到 " + totalCount + " 条相关商品" : "未找到相关商品，可尝试放宽筛选条件");
        response.setProducts(pageList);
        response.setTotalCount(totalCount);

        response.setSimilarSearches(Arrays.asList(
                Map.of("query", (category == null ? "热门" : category + " 相关"), "count", totalCount),
                Map.of("query", "校园二手", "count", totalCount)
        ));

        log.info("搜索结果 - keyword={}, total={}, 返回={}", keyword, totalCount, pageList.size());

        return response;
    }
}
