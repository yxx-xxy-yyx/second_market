package com.echoofmemories.project.service;

import com.echoofmemories.project.dto.*;

import java.util.List;

/**
 * 统计服务接口
 * 
 * @author EchoOfMemories
 */
public interface StatisticsService {
    
    StatisticsOverviewDTO getOverviewStatistics();
    
    List<TrendDataDTO> getUserTrend(Integer days);
    
    List<CategoryStatisticsDTO> getCategoryStatistics();
    
    List<OrderStatusStatisticsDTO> getOrderStatusStatistics();
    
    List<TradeTrendDTO> getTradeTrend(Integer days);
    
    List<HotProductDTO> getHotProducts(Integer limit);
    
    List<ActiveUserDTO> getActiveUsers(Integer limit);
    
    UserStatisticsOverviewDTO getUserOverviewStatistics(Long userId);
    
    List<IncomeExpenseDTO> getUserIncome(Long userId, Integer days);
    
    List<IncomeExpenseDTO> getUserExpense(Long userId, Integer days);
    
    List<ProductViewTrendDTO> getUserProductViewTrend(Long userId, Integer limit);
    
    UserRatingDTO getUserRating(Long userId);
}

