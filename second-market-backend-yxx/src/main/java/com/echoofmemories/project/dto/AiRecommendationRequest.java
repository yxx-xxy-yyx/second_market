package com.echoofmemories.project.dto;

import lombok.Data;

/**
 * AI推荐请求
 */
@Data
public class AiRecommendationRequest {
    
    /**
     * 推荐类型：home-首页推荐，cart-购物车推荐，product-商品详情页推荐
     */
    private String type = "home";
    
    /**
     * 商品ID（当type为product时需要）
     */
    private Long productId;
    
    /**
     * 推荐数量
     */
    private Integer limit = 10;
}