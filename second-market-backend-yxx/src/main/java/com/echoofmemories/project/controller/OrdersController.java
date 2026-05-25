package com.echoofmemories.project.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.echoofmemories.project.common.Result;
import com.echoofmemories.project.dto.OrderCreateRequest;
import com.echoofmemories.project.dto.OrderPageRequest;
import com.echoofmemories.project.entity.Orders;
import com.echoofmemories.project.security.SecurityUtils;
import com.echoofmemories.project.service.OrdersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 订单控制器
 * 
 * @author EchoOfMemories
 */
@Tag(name = "订单管理", description = "订单相关接口")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrdersController {

    private final OrdersService ordersService;

    @Operation(summary = "创建订单")
    @PostMapping("/create")
    public Result<Orders> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.error("401", "用户未认证");
            }

            Orders order = ordersService.createOrder(userId, request.getProductId());
            if (request.getRemark() != null) {
                order.setRemark(request.getRemark());
                ordersService.updateById(order);
            }
            return Result.success("订单创建成功", order);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "支付订单")
    @PutMapping("/pay/{id}")
    public Result<String> payOrder(@PathVariable Long id) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.error("401", "用户未认证");
            }

            boolean success = ordersService.payOrder(id, userId);
            if (success) {
                return Result.success("支付成功");
            } else {
                return Result.error("支付失败");
            }
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "完成订单")
    @PutMapping("/finish/{id}")
    public Result<String> finishOrder(@PathVariable Long id) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.error("401", "用户未认证");
            }

            boolean success = ordersService.finishOrder(id, userId);
            if (success) {
                return Result.success("订单已完成");
            } else {
                return Result.error("操作失败");
            }
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "取消订单")
    @PutMapping("/cancel/{id}")
    public Result<String> cancelOrder(@PathVariable Long id) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.error("401", "用户未认证");
            }

            boolean success = ordersService.cancelOrder(id, userId);
            if (success) {
                return Result.success("订单已取消");
            } else {
                return Result.error("操作失败");
            }
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "获取订单详情")
    @GetMapping("/{id}")
    public Result<Orders> getOrder(@PathVariable Long id) {
        try {
            Orders order = ordersService.getOrderWithDetailsById(id);
            return Result.success(order);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "我的购买订单列表")
    @PostMapping("/my/buyer")
    public Result<Page<Orders>> getBuyerOrders(@RequestBody OrderPageRequest request) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.error("401", "用户未认证");
            }

            Page<Orders> page = ordersService.getBuyerOrderPage(
                request.getPageNum(),
                request.getPageSize(),
                userId,
                request.getStatus()
            );
            return Result.success(page);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "我的销售订单列表")
    @PostMapping("/my/seller")
    public Result<Page<Orders>> getSellerOrders(@RequestBody OrderPageRequest request) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.error("401", "用户未认证");
            }

            Page<Orders> page = ordersService.getSellerOrderPage(
                request.getPageNum(),
                request.getPageSize(),
                userId,
                request.getStatus()
            );
            return Result.success(page);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }
}

