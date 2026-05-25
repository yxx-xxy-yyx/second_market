package com.echoofmemories.project.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.echoofmemories.project.common.Result;
import com.echoofmemories.project.entity.Favorite;
import com.echoofmemories.project.security.SecurityUtils;
import com.echoofmemories.project.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 收藏控制器
 * 
 * @author EchoOfMemories
 */
@Tag(name = "收藏管理", description = "收藏相关接口")
@RestController
@RequestMapping("/favorite")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Operation(summary = "添加收藏")
    @PostMapping("/add")
    public Result<String> addFavorite(@RequestParam Long productId) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.error("401", "用户未认证");
            }
            
            boolean success = favoriteService.addFavorite(userId, productId);
            return success ? Result.success("收藏成功") : Result.error("收藏失败");
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "取消收藏")
    @DeleteMapping("/delete/{productId}")
    public Result<String> deleteFavorite(@PathVariable Long productId) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.error("401", "用户未认证");
            }
            
            boolean success = favoriteService.deleteFavorite(userId, productId);
            return success ? Result.success("取消收藏成功") : Result.error("取消收藏失败");
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "我的收藏列表")
    @PostMapping("/my")
    public Result<Page<Favorite>> getMyFavorites(@RequestBody(required = false) java.util.Map<String, Object> params) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.error("401", "用户未认证");
            }
            
            Integer pageNum = params != null && params.get("pageNum") != null 
                ? Integer.valueOf(params.get("pageNum").toString()) : 1;
            Integer pageSize = params != null && params.get("pageSize") != null 
                ? Integer.valueOf(params.get("pageSize").toString()) : 10;
            
            Page<Favorite> page = favoriteService.getMyFavoritePage(pageNum, pageSize, userId);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "检查是否已收藏")
    @GetMapping("/check/{productId}")
    public Result<Boolean> checkFavorite(@PathVariable Long productId) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.success(false);
            }
            
            boolean isFavorite = favoriteService.isFavorite(userId, productId);
            return Result.success(isFavorite);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }
}

