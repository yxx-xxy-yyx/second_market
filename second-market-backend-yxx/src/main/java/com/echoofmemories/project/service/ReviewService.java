package com.echoofmemories.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.echoofmemories.project.dto.ErrandReviewRequest;
import com.echoofmemories.project.dto.ReviewRequest;
import com.echoofmemories.project.entity.Review;

import java.util.Map;

/**
 * 评价服务接口
 * 
 * @author EchoOfMemories
 */
public interface ReviewService extends IService<Review> {
    
    Review addReview(ReviewRequest reviewRequest, Long userId);

    Review addErrandReview(ErrandReviewRequest reviewRequest, Long userId);
    
    Page<Review> getProductReviewPage(Integer pageNum, Integer pageSize, Long productId);

    Page<Review> getCampusServiceReviewPage(Integer pageNum, Integer pageSize, Long campusServiceId);
    
    Page<Review> getUserReviewPage(Integer pageNum, Integer pageSize, Long userId);
    
    Map<String, Object> getUserRatingInfo(Long userId);

    boolean hasReviewedOrder(Long orderId, Long userId);

    boolean hasReviewedCampusService(Long campusServiceId, Long userId);
}

