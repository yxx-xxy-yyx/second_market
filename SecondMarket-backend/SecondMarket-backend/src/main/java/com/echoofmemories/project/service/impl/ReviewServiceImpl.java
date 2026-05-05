package com.echoofmemories.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.echoofmemories.project.dto.ReviewRequest;
import com.echoofmemories.project.entity.Orders;
import com.echoofmemories.project.entity.Review;
import com.echoofmemories.project.exception.CustomException;
import com.echoofmemories.project.mapper.ReviewMapper;
import com.echoofmemories.project.service.OrdersService;
import com.echoofmemories.project.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 评价服务实现类
 * 
 * @author EchoOfMemories
 */
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final OrdersService ordersService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Review addReview(ReviewRequest reviewRequest, Long userId) {
        Orders order = ordersService.getById(reviewRequest.getOrderId());
        
        if (order == null) {
            throw new CustomException("订单不存在");
        }

        if (!order.getBuyerId().equals(userId)) {
            throw new CustomException("只能评价自己的订单");
        }

        if (order.getStatus() != 2) {
            throw new CustomException("只能评价已完成的订单");
        }

        LambdaQueryWrapper<Review> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Review::getOrderId, reviewRequest.getOrderId())
                    .eq(Review::getDeleted, 0);
        Review existingReview = this.getOne(queryWrapper);

        if (existingReview != null) {
            throw new CustomException("该订单已评价");
        }

        Review review = new Review();
        review.setOrderId(reviewRequest.getOrderId());
        review.setReviewerId(userId);
        review.setRevieweeId(order.getSellerId());
        review.setProductId(order.getProductId());
        review.setRating(reviewRequest.getRating());
        review.setContent(reviewRequest.getContent());
        review.setDeleted(0);

        this.save(review);
        return review;
    }

    @Override
    public Page<Review> getProductReviewPage(Integer pageNum, Integer pageSize, Long productId) {
        Page<Review> page = new Page<>(pageNum, pageSize);
        return reviewMapper.selectReviewPageByProduct(page, productId);
    }

    @Override
    public Page<Review> getUserReviewPage(Integer pageNum, Integer pageSize, Long userId) {
        Page<Review> page = new Page<>(pageNum, pageSize);
        return reviewMapper.selectReviewPageByUser(page, userId);
    }

    @Override
    public Map<String, Object> getUserRatingInfo(Long userId) {
        return reviewMapper.selectAvgRatingByUser(userId);
    }
}

