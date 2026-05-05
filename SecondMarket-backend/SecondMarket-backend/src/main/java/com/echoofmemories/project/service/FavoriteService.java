package com.echoofmemories.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.echoofmemories.project.entity.Favorite;

/**
 * 收藏服务接口
 * 
 * @author EchoOfMemories
 */
public interface FavoriteService extends IService<Favorite> {
    
    boolean addFavorite(Long userId, Long productId);
    
    boolean deleteFavorite(Long userId, Long productId);
    
    Page<Favorite> getMyFavoritePage(Integer pageNum, Integer pageSize, Long userId);
    
    boolean isFavorite(Long userId, Long productId);
}

