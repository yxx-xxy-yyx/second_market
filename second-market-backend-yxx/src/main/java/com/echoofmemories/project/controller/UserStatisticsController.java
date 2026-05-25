package com.echoofmemories.project.controller;

import com.echoofmemories.project.common.Result;
import com.echoofmemories.project.dto.*;
import com.echoofmemories.project.security.SecurityUtils;
import com.echoofmemories.project.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户统计控制器
 * 
 * @author EchoOfMemories
 */
@Tag(name = "用户统计", description = "用户个人统计接口")
@RestController
@RequestMapping("/user/statistics")
@RequiredArgsConstructor
public class UserStatisticsController {

    private final StatisticsService statisticsService;

    @Operation(summary = "个人数据概览")
    @GetMapping("/overview")
    public Result<UserStatisticsOverviewDTO> getOverview() {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.error("401", "用户未认证");
            }

            UserStatisticsOverviewDTO overview = statisticsService.getUserOverviewStatistics(userId);
            return Result.success(overview);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "我的收入统计")
    @GetMapping("/trade/income")
    public Result<List<IncomeExpenseDTO>> getIncome(@RequestParam(defaultValue = "30") Integer days) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.error("401", "用户未认证");
            }

            List<IncomeExpenseDTO> income = statisticsService.getUserIncome(userId, days);
            return Result.success(income);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "我的支出统计")
    @GetMapping("/trade/expense")
    public Result<List<IncomeExpenseDTO>> getExpense(@RequestParam(defaultValue = "30") Integer days) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.error("401", "用户未认证");
            }

            List<IncomeExpenseDTO> expense = statisticsService.getUserExpense(userId, days);
            return Result.success(expense);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "商品浏览趋势")
    @GetMapping("/product/view")
    public Result<List<ProductViewTrendDTO>> getProductViewTrend(@RequestParam(defaultValue = "10") Integer limit) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.error("401", "用户未认证");
            }

            List<ProductViewTrendDTO> trend = statisticsService.getUserProductViewTrend(userId, limit);
            return Result.success(trend);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "我的信用评分")
    @GetMapping("/rating")
    public Result<UserRatingDTO> getRating() {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.error("401", "用户未认证");
            }

            UserRatingDTO rating = statisticsService.getUserRating(userId);
            return Result.success(rating);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }
}

