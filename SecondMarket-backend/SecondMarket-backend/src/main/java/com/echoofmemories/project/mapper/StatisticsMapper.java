package com.echoofmemories.project.mapper;

import com.echoofmemories.project.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 统计数据访问层
 * 
 * @author EchoOfMemories
 */
@Mapper
public interface StatisticsMapper {
    
    StatisticsOverviewDTO selectOverviewStatistics();
    
    List<TrendDataDTO> selectUserTrend(@Param("days") Integer days);
    
    List<CategoryStatisticsDTO> selectCategoryStatistics();
    
    List<OrderStatusStatisticsDTO> selectOrderStatusStatistics();
    
    List<TradeTrendDTO> selectTradeTrend(@Param("days") Integer days);
    
    List<HotProductDTO> selectHotProducts(@Param("limit") Integer limit);
    
    List<ActiveUserDTO> selectActiveUsers(@Param("limit") Integer limit);
    
    UserStatisticsOverviewDTO selectUserOverviewStatistics(@Param("userId") Long userId);
    
    List<IncomeExpenseDTO> selectUserIncome(@Param("userId") Long userId, @Param("days") Integer days);
    
    List<IncomeExpenseDTO> selectUserExpense(@Param("userId") Long userId, @Param("days") Integer days);
    
    List<ProductViewTrendDTO> selectUserProductViewTrend(@Param("userId") Long userId, @Param("limit") Integer limit);
    
    UserRatingDTO selectUserRating(@Param("userId") Long userId);
}

