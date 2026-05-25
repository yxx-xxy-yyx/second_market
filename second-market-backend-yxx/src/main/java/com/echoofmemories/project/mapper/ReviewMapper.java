package com.echoofmemories.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.echoofmemories.project.entity.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 评价数据访问层
 * 
 * @author EchoOfMemories
 */
@Mapper
public interface ReviewMapper extends BaseMapper<Review> {
    
    Page<Review> selectReviewPageByProduct(Page<Review> page, @Param("productId") Long productId);
    
    Page<Review> selectReviewPageByUser(Page<Review> page, @Param("userId") Long userId);

    Page<Review> selectReviewPageByCampusService(Page<Review> page, @Param("campusServiceId") Long campusServiceId);
    
    Map<String, Object> selectAvgRatingByUser(@Param("userId") Long userId);
}

