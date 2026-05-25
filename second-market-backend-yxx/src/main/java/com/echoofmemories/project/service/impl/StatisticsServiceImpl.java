package com.echoofmemories.project.service.impl;

import com.echoofmemories.project.dto.*;
import com.echoofmemories.project.mapper.StatisticsMapper;
import com.echoofmemories.project.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 统计服务实现类
 * 
 * @author EchoOfMemories
 */
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final StatisticsMapper statisticsMapper;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public StatisticsOverviewDTO getOverviewStatistics() {
        return statisticsMapper.selectOverviewStatistics();
    }

    @Override
    public List<TrendDataDTO> getUserTrend(Integer days) {
        List<TrendDataDTO> data = statisticsMapper.selectUserTrend(days);
        return fillMissingDates(data, days);
    }

    @Override
    public List<CategoryStatisticsDTO> getCategoryStatistics() {
        return statisticsMapper.selectCategoryStatistics();
    }

    @Override
    public List<OrderStatusStatisticsDTO> getOrderStatusStatistics() {
        return statisticsMapper.selectOrderStatusStatistics();
    }

    @Override
    public List<TradeTrendDTO> getTradeTrend(Integer days) {
        List<TradeTrendDTO> data = statisticsMapper.selectTradeTrend(days);
        return fillMissingTradeDates(data, days);
    }

    @Override
    public List<HotProductDTO> getHotProducts(Integer limit) {
        return statisticsMapper.selectHotProducts(limit);
    }

    @Override
    public List<ActiveUserDTO> getActiveUsers(Integer limit) {
        return statisticsMapper.selectActiveUsers(limit);
    }

    private List<TrendDataDTO> fillMissingDates(List<TrendDataDTO> data, Integer days) {
        Map<String, Long> dataMap = data.stream()
                .collect(Collectors.toMap(TrendDataDTO::getDate, TrendDataDTO::getCount));

        List<TrendDataDTO> result = new ArrayList<>();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            String dateStr = date.format(DATE_FORMATTER);
            Long count = dataMap.getOrDefault(dateStr, 0L);
            result.add(new TrendDataDTO(dateStr, count));
        }

        return result;
    }

    private List<TradeTrendDTO> fillMissingTradeDates(List<TradeTrendDTO> data, Integer days) {
        Map<String, TradeTrendDTO> dataMap = data.stream()
                .collect(Collectors.toMap(TradeTrendDTO::getDate, dto -> dto));

        List<TradeTrendDTO> result = new ArrayList<>();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            String dateStr = date.format(DATE_FORMATTER);
            TradeTrendDTO dto = dataMap.get(dateStr);
            if (dto != null) {
                result.add(dto);
            } else {
                result.add(new TradeTrendDTO(dateStr, 0L, BigDecimal.ZERO));
            }
        }

        return result;
    }

    @Override
    public UserStatisticsOverviewDTO getUserOverviewStatistics(Long userId) {
        return statisticsMapper.selectUserOverviewStatistics(userId);
    }

    @Override
    public List<IncomeExpenseDTO> getUserIncome(Long userId, Integer days) {
        List<IncomeExpenseDTO> data = statisticsMapper.selectUserIncome(userId, days);
        return fillMissingIncomeDates(data, days);
    }

    @Override
    public List<IncomeExpenseDTO> getUserExpense(Long userId, Integer days) {
        List<IncomeExpenseDTO> data = statisticsMapper.selectUserExpense(userId, days);
        return fillMissingIncomeDates(data, days);
    }

    @Override
    public List<ProductViewTrendDTO> getUserProductViewTrend(Long userId, Integer limit) {
        return statisticsMapper.selectUserProductViewTrend(userId, limit);
    }

    @Override
    public UserRatingDTO getUserRating(Long userId) {
        return statisticsMapper.selectUserRating(userId);
    }

    private List<IncomeExpenseDTO> fillMissingIncomeDates(List<IncomeExpenseDTO> data, Integer days) {
        Map<String, BigDecimal> dataMap = data.stream()
                .collect(Collectors.toMap(IncomeExpenseDTO::getDate, IncomeExpenseDTO::getAmount));

        List<IncomeExpenseDTO> result = new ArrayList<>();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            String dateStr = date.format(DATE_FORMATTER);
            BigDecimal amount = dataMap.getOrDefault(dateStr, BigDecimal.ZERO);
            result.add(new IncomeExpenseDTO(dateStr, amount));
        }

        return result;
    }
}

