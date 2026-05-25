package com.echoofmemories.project.controller;

import com.echoofmemories.project.common.Result;
import com.echoofmemories.project.dto.*;
import com.echoofmemories.project.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 统计分析控制器
 * 
 * @author EchoOfMemories
 */
@Tag(name = "统计分析", description = "管理员统计分析接口")
@RestController
@RequestMapping("/admin/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Operation(summary = "平台概览统计")
    @GetMapping("/overview")
    public Result<StatisticsOverviewDTO> getOverview() {
        try {
            StatisticsOverviewDTO overview = statisticsService.getOverviewStatistics();
            return Result.success(overview);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "用户增长趋势")
    @GetMapping("/user/trend")
    public Result<List<TrendDataDTO>> getUserTrend(@RequestParam(defaultValue = "7") Integer days) {
        try {
            List<TrendDataDTO> trend = statisticsService.getUserTrend(days);
            return Result.success(trend);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "商品分类统计")
    @GetMapping("/product/category")
    public Result<List<CategoryStatisticsDTO>> getCategoryStatistics() {
        try {
            List<CategoryStatisticsDTO> statistics = statisticsService.getCategoryStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "订单状态分布")
    @GetMapping("/order/status")
    public Result<List<OrderStatusStatisticsDTO>> getOrderStatusStatistics() {
        try {
            List<OrderStatusStatisticsDTO> statistics = statisticsService.getOrderStatusStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "交易趋势统计")
    @GetMapping("/trade/trend")
    public Result<List<TradeTrendDTO>> getTradeTrend(@RequestParam(defaultValue = "30") Integer days) {
        try {
            List<TradeTrendDTO> trend = statisticsService.getTradeTrend(days);
            return Result.success(trend);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "热门商品Top10")
    @GetMapping("/product/hot")
    public Result<List<HotProductDTO>> getHotProducts(@RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<HotProductDTO> hotProducts = statisticsService.getHotProducts(limit);
            return Result.success(hotProducts);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "活跃用户Top10")
    @GetMapping("/user/active")
    public Result<List<ActiveUserDTO>> getActiveUsers(@RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<ActiveUserDTO> activeUsers = statisticsService.getActiveUsers(limit);
            return Result.success(activeUsers);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }
}

