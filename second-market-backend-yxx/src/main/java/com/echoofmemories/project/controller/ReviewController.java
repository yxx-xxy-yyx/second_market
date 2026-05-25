package com.echoofmemories.project.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.echoofmemories.project.common.Result;
import com.echoofmemories.project.dto.ErrandReviewRequest;
import com.echoofmemories.project.dto.ReviewRequest;
import com.echoofmemories.project.entity.Review;
import com.echoofmemories.project.security.SecurityUtils;
import com.echoofmemories.project.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * 评价控制器
 * 
 * @author EchoOfMemories
 */
@Tag(name = "评价管理", description = "评价相关接口")
@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "发表评价")
    @PostMapping("/add")
    public Result<Review> addReview(@Valid @RequestBody ReviewRequest reviewRequest) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.error("401", "用户未认证");
            }
            
            Review review = reviewService.addReview(reviewRequest, userId);
            return Result.success("评价成功", review);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "检查订单是否已评价")
    @GetMapping("/check/{orderId}")
    public Result<Boolean> checkReviewed(@PathVariable Long orderId) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.error("401", "用户未认证");
            }
            return Result.success(reviewService.hasReviewedOrder(orderId, userId));
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "发表跑腿评价")
    @PostMapping("/errand/add")
    public Result<Review> addErrandReview(@Valid @RequestBody ErrandReviewRequest reviewRequest) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.error("401", "用户未认证");
            }
            Review review = reviewService.addErrandReview(reviewRequest, userId);
            return Result.success("评价成功", review);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "获取跑腿订单的评价列表")
    @GetMapping("/errand/{campusServiceId}")
    public Result<Page<Review>> getCampusServiceReviews(@PathVariable Long campusServiceId,
                                                        @RequestParam(defaultValue = "1") Integer pageNum,
                                                        @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<Review> page = reviewService.getCampusServiceReviewPage(pageNum, pageSize, campusServiceId);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "检查跑腿订单是否已评价")
    @GetMapping("/errand/check/{campusServiceId}")
    public Result<Boolean> checkErrandReviewed(@PathVariable Long campusServiceId) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.error("401", "用户未认证");
            }
            return Result.success(reviewService.hasReviewedCampusService(campusServiceId, userId));
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "获取商品的评价列表")
    @GetMapping("/product/{productId}")
    public Result<Page<Review>> getProductReviews(@PathVariable Long productId,
                                                   @RequestParam(defaultValue = "1") Integer pageNum,
                                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<Review> page = reviewService.getProductReviewPage(pageNum, pageSize, productId);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "获取用户收到的评价列表")
    @GetMapping("/user/{userId}")
    public Result<Page<Review>> getUserReviews(@PathVariable Long userId,
                                                @RequestParam(defaultValue = "1") Integer pageNum,
                                                @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<Review> page = reviewService.getUserReviewPage(pageNum, pageSize, userId);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "获取用户评分信息")
    @GetMapping("/rating/{userId}")
    public Result<Map<String, Object>> getUserRating(@PathVariable Long userId) {
        try {
            Map<String, Object> ratingInfo = reviewService.getUserRatingInfo(userId);
            return Result.success(ratingInfo);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }
}

