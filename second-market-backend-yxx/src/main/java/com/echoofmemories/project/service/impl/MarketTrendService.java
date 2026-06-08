package com.echoofmemories.project.service.impl;

import com.echoofmemories.project.dto.AiMarketTrendRequest;
import com.echoofmemories.project.dto.AiMarketTrendResponse;
import com.echoofmemories.project.entity.Orders;
import com.echoofmemories.project.entity.Product;
import com.echoofmemories.project.mapper.OrdersMapper;
import com.echoofmemories.project.mapper.ProductMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketTrendService {

    private final ProductMapper productMapper;
    private final OrdersMapper ordersMapper;
    private final LlmClientService llmClient;

    public AiMarketTrendResponse getMarketTrend(AiMarketTrendRequest request, Long userId) {
        log.info("市场行情参考 - 用户ID: {}, 品类: {}", userId, request.getCategory());

        AiMarketTrendResponse response = new AiMarketTrendResponse();

        String category = request.getCategory();
        int days = request.getDays() == null || request.getDays() <= 0 ? 7 : request.getDays();

        double avgPrice = 0.0;
        double lowPrice = 0.0;
        double highPrice = 0.0;
        int productCount = 0;
        try {
            QueryWrapper<Product> pw = new QueryWrapper<>();
            pw.eq("deleted", 0);
            if (category != null && !category.isEmpty()) {
                pw.eq("category", category);
            }
            pw.isNotNull("price");
            List<Product> products = productMapper.selectList(pw);
            if (!products.isEmpty()) {
                productCount = products.size();
                BigDecimal total = BigDecimal.ZERO;
                BigDecimal min = products.get(0).getPrice();
                BigDecimal max = products.get(0).getPrice();
                for (Product p : products) {
                    if (p.getPrice() == null) continue;
                    total = total.add(p.getPrice());
                    if (p.getPrice().compareTo(min) < 0) min = p.getPrice();
                    if (p.getPrice().compareTo(max) > 0) max = p.getPrice();
                }
                avgPrice = total.divide(new BigDecimal(products.size()), 2, RoundingMode.HALF_UP).doubleValue();
                lowPrice = min.doubleValue();
                highPrice = max.doubleValue();
            }
        } catch (Exception e) {
            log.warn("品类商品价格聚合失败: {}", e.getMessage());
        }
        response.setAveragePrice(avgPrice);
        response.setLowestPrice(lowPrice);
        response.setHighestPrice(highPrice);

        int totalSold = 0;
        try {
            QueryWrapper<Orders> ow = new QueryWrapper<>();
            ow.eq("deleted", 0);
            if (category != null && !category.isEmpty()) {
                ow.inSql("product_id", "SELECT id FROM product WHERE deleted=0 AND category='"
                        + category.replace("'", "") + "'");
            }
            ow.eq("status", 4);
            Long count = ordersMapper.selectCount(ow);
            totalSold = count == null ? 0 : count.intValue();
        } catch (Exception e) {
            log.warn("成交数量统计失败: {}", e.getMessage());
        }
        response.setTotalSold(totalSold);

        List<Map<String, Object>> priceTrend = new ArrayList<>();
        try {
            LocalDate today = LocalDate.now();
            Map<LocalDate, BigDecimal> dayTotal = new HashMap<>();
            Map<LocalDate, Integer> dayCount = new HashMap<>();
            QueryWrapper<Orders> ow2 = new QueryWrapper<>();
            ow2.eq("deleted", 0).eq("status", 4);
            if (category != null && !category.isEmpty()) {
                ow2.inSql("product_id", "SELECT id FROM product WHERE deleted=0 AND category='"
                        + category.replace("'", "") + "'");
            }
            ow2.ge("create_time", today.minusDays(days).atStartOfDay());
            ow2.le("create_time", today.atTime(23, 59, 59));
            List<Orders> recentOrders = ordersMapper.selectList(ow2);
            for (Orders o : recentOrders) {
                if (o.getCreateTime() == null || o.getAmount() == null) continue;
                LocalDate d = o.getCreateTime().toLocalDate();
                dayTotal.put(d, dayTotal.getOrDefault(d, BigDecimal.ZERO).add(o.getAmount()));
                dayCount.put(d, dayCount.getOrDefault(d, 0) + 1);
            }
            for (int i = days - 1; i >= 0; i--) {
                LocalDate d = today.minusDays(i);
                Map<String, Object> row = new HashMap<>();
                row.put("date", d.toString());
                BigDecimal t = dayTotal.get(d);
                Integer c = dayCount.get(d);
                if (t != null && c != null && c > 0) {
                    row.put("price", t.divide(new BigDecimal(c), 2, RoundingMode.HALF_UP).doubleValue());
                    row.put("count", c);
                } else {
                    row.put("price", 0);
                    row.put("count", 0);
                }
                priceTrend.add(row);
            }
        } catch (Exception e) {
            log.warn("价格走势统计失败: {}", e.getMessage());
        }
        response.setPriceTrend(priceTrend);

        List<Map<String, Object>> similarProducts = new ArrayList<>();
        try {
            QueryWrapper<Product> pw2 = new QueryWrapper<>();
            pw2.eq("deleted", 0);
            if (category != null && !category.isEmpty()) {
                pw2.eq("category", category);
            }
            pw2.orderByDesc("(view_count + favorite_count)");
            pw2.last("LIMIT 5");
            List<Product> top = productMapper.selectList(pw2);
            for (Product p : top) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", p.getId());
                m.put("name", p.getTitle());
                m.put("price", p.getPrice() == null ? 0 : p.getPrice().doubleValue());
                m.put("condition", p.getConditionScore() == null ? 0 : p.getConditionScore());
                m.put("viewCount", p.getViewCount() == null ? 0 : p.getViewCount());
                m.put("favoriteCount", p.getFavoriteCount() == null ? 0 : p.getFavoriteCount());
                similarProducts.add(m);
            }
        } catch (Exception e) {
            log.warn("热门同类商品查询失败: {}", e.getMessage());
        }
        response.setSimilarProducts(similarProducts);

        StringBuilder recommendation = new StringBuilder();
        recommendation.append("品类").append(category == null ? "全部" : category)
                .append(" - 当前均价 ¥").append(String.format("%.2f", avgPrice))
                .append("（").append(productCount).append("件在售，近").append(days).append("天成交")
                .append(totalSold).append("单）。");
        if (avgPrice > 0) {
            if (totalSold >= productCount * 0.3) {
                recommendation.append(" 成交活跃，建议关注热门同类商品。");
            } else if (totalSold >= productCount * 0.1) {
                recommendation.append(" 需求稳定，价格可参考均价调整。");
            } else {
                recommendation.append(" 成交一般，建议优化商品描述或适度降价。");
            }
        }
        response.setRecommendation(recommendation.toString());

        String priceSuggestion;
        if (avgPrice <= 0) {
            priceSuggestion = "暂无同类成交数据，建议参考商品成色与市场评估定价";
        } else {
            priceSuggestion = "推荐定价参考同类均价 ¥" + String.format("%.2f", avgPrice)
                    + "，区间 ¥" + String.format("%.2f", lowPrice) + " ~ ¥" + String.format("%.2f", highPrice);
        }
        response.setPriceSuggestion(priceSuggestion);

        StringBuilder outlook = new StringBuilder();
        outlook.append("未来").append(days).append("天");
        if (totalSold > productCount * 0.2 && avgPrice > 0) {
            outlook.append("市场需求旺盛，价格预计保持平稳或小幅上涨，建议尽快发布或上架");
        } else if (totalSold > 0) {
            outlook.append("需求一般，建议关注同类TOP商品走势并优化商品详情页");
        } else {
            outlook.append("近期无成交数据，建议先完善商品信息并适当降低价格以吸引买家");
        }
        response.setMarketOutlook(outlook.toString());

        log.info("行情结果 - 品类={}, days={}, 均价={}, 最低={}, 最高={}, 成交={}, similarProducts={}",
                category, days, avgPrice, lowPrice, highPrice, totalSold, similarProducts.size());

        return response;
    }
}
