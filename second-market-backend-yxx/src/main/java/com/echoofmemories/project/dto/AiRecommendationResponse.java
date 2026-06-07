package com.echoofmemories.project.dto;

import com.echoofmemories.project.entity.Product;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * AI推荐响应
 */
@Data
public class AiRecommendationResponse {
    
    /**
     * 是否成功
     */
    private Boolean success;
    
    /**
     * 推荐商品列表
     */
    private List<ProductWithReason> products;
    
    /**
     * 推荐说明
     */
    private String explanation;
    
    /**
     * 用户兴趣标签
     */
    private List<String> interestTags;
    
    /**
     * AI生成的推荐策略说明
     */
    private String aiReasoning;
    
    /**
     * 响应时间（毫秒）
     */
    private Long responseTime;
    
    @Data
    public static class ProductWithReason {
        // Product 字段 - 直接字段直接直接 Product 对象，
        private Long id;
        private String title;
        private String titleKo;
        private String description;
        private String category;
        private java.math.BigDecimal price;
        private String images;
        private Integer viewCount;
        private Product product;
        private String reason; // 推荐理由
        private Double matchScore; // 匹配度分数
        private String reasonType; // 推荐理由类型：browse_history-浏览历史相关，search-搜索相关，similar-相似商品，hot-热门商品
    }
}