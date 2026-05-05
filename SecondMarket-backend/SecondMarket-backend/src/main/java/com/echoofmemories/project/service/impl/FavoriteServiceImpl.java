package com.echoofmemories.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.echoofmemories.project.entity.Favorite;
import com.echoofmemories.project.entity.Product;
import com.echoofmemories.project.exception.CustomException;
import com.echoofmemories.project.mapper.FavoriteMapper;
import com.echoofmemories.project.service.FavoriteService;
import com.echoofmemories.project.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 收藏服务实现类
 * 
 * @author EchoOfMemories
 */
@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final ProductService productService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addFavorite(Long userId, Long productId) {
        Product product = productService.getById(productId);
        if (product == null || product.getDeleted() == 1) {
            throw new CustomException("商品不存在");
        }

        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, userId)
                    .eq(Favorite::getProductId, productId)
                    .eq(Favorite::getDeleted, 0);
        Favorite existingFavorite = this.getOne(queryWrapper);

        if (existingFavorite != null) {
            throw new CustomException("已收藏该商品");
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        favorite.setDeleted(0);

        boolean saved = this.save(favorite);
        
        if (saved) {
            product.setFavoriteCount(product.getFavoriteCount() + 1);
            productService.updateById(product);
        }

        return saved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteFavorite(Long userId, Long productId) {
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, userId)
                    .eq(Favorite::getProductId, productId)
                    .eq(Favorite::getDeleted, 0);
        Favorite favorite = this.getOne(queryWrapper);

        if (favorite == null) {
            throw new CustomException("未收藏该商品");
        }

        boolean removed = this.removeById(favorite.getId());
        
        if (removed) {
            Product product = productService.getById(productId);
            if (product != null && product.getFavoriteCount() > 0) {
                product.setFavoriteCount(product.getFavoriteCount() - 1);
                productService.updateById(product);
            }
        }

        return removed;
    }

    @Override
    public Page<Favorite> getMyFavoritePage(Integer pageNum, Integer pageSize, Long userId) {
        Page<Favorite> page = new Page<>(pageNum, pageSize);
        return favoriteMapper.selectFavoritePageWithProduct(page, userId);
    }

    @Override
    public boolean isFavorite(Long userId, Long productId) {
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, userId)
                    .eq(Favorite::getProductId, productId)
                    .eq(Favorite::getDeleted, 0);
        return this.count(queryWrapper) > 0;
    }
}

