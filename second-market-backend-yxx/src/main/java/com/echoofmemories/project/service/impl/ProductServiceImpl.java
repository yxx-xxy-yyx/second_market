package com.echoofmemories.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.echoofmemories.project.entity.Product;
import com.echoofmemories.project.entity.User;
import com.echoofmemories.project.exception.CustomException;
import com.echoofmemories.project.mapper.ProductMapper;
import com.echoofmemories.project.mapper.UserMapper;
import com.echoofmemories.project.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 商品服务实现类
 * 
 * @author EchoOfMemories
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final ProductMapper productMapper;
    private final UserMapper userMapper;
    
    private static final Map<String, Long> VIEW_CACHE = new ConcurrentHashMap<>();
    private static final long VIEW_INTERVAL = 5 * 60 * 1000;
    private ScheduledExecutorService cacheCleanerExecutor;

    @PostConstruct
    public void init() {
        cacheCleanerExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "view-cache-cleaner");
            t.setDaemon(true);
            return t;
        });
        // 每10分钟清理一次过期缓存
        cacheCleanerExecutor.scheduleAtFixedRate(this::cleanExpiredCache, 10, 10, TimeUnit.MINUTES);
    }

    @PreDestroy
    public void destroy() {
        if (cacheCleanerExecutor != null) {
            cacheCleanerExecutor.shutdown();
        }
    }

    @Override
    public Page<Product> getProductPage(Integer pageNum, Integer pageSize, String keyword, 
                                       String category, BigDecimal minPrice, BigDecimal maxPrice, 
                                       Integer minCondition, Integer maxCondition, String sortBy,
                                       Integer status, Long userId, Long schoolId) {
        Page<Product> page = new Page<>(pageNum, pageSize);
        return productMapper.selectProductPageWithUser(page, keyword, category, minPrice, maxPrice, 
                                                      minCondition, maxCondition, sortBy, status, userId, schoolId);
    }

    @Override
    public Product getProductWithUserById(Long id) {
        Product product = productMapper.selectProductWithUserById(id);
        if (product == null) {
            throw new CustomException("商品不存在");
        }
        return product;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addProduct(Product product) {
        if (product.getSchoolId() == null) {
            User user = userMapper.selectById(product.getUserId());
            if (user != null) {
                product.setSchoolId(user.getSchoolId());
            }
        }
        if (product.getStatus() == null) {
            product.setStatus(1);
        }
        if (product.getViewCount() == null) {
            product.setViewCount(0);
        }
        if (product.getFavoriteCount() == null) {
            product.setFavoriteCount(0);
        }
        if (product.getAiAnalyzed() == null) {
            product.setAiAnalyzed(0);
        }
        if (product.getConditionScore() == null) {
            product.setConditionScore(8);
        }
        
        return this.save(product);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateProduct(Product product) {
        Product existProduct = this.getById(product.getId());
        if (existProduct == null) {
            throw new CustomException("商品不存在");
        }
        
        if (existProduct.getDeleted() == 1) {
            throw new CustomException("商品已删除");
        }
        
        return this.updateById(product);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteProduct(Long id) {
        Product product = this.getById(id);
        if (product == null) {
            throw new CustomException("商品不存在");
        }
        
        return this.removeById(id);
    }

    @Override
    @Transactional
    public Product viewProduct(Long productId, String userIdentifier) {
        Product product = getById(productId);
        if (product == null || product.getDeleted() == 1) {
            throw new CustomException("商品不存在");
        }

        String cacheKey = productId + "_" + userIdentifier;
        Long lastViewTime = VIEW_CACHE.get(cacheKey);
        long currentTime = System.currentTimeMillis();

        if (lastViewTime == null || (currentTime - lastViewTime) > VIEW_INTERVAL) {
            LambdaUpdateWrapper<Product> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Product::getId, productId)
                        .setSql("view_count = view_count + 1");
            this.update(updateWrapper);

            VIEW_CACHE.put(cacheKey, currentTime);

            product = getById(productId);
        }

        return product;
    }

    private void cleanExpiredCache() {
        log.debug("开始清理过期的视图浏览缓存...");
        long currentTime = System.currentTimeMillis();
        int initialSize = VIEW_CACHE.size();
        VIEW_CACHE.entrySet().removeIf(entry -> 
            (currentTime - entry.getValue()) > VIEW_INTERVAL);
        int removedCount = initialSize - VIEW_CACHE.size();
        if (removedCount > 0) {
            log.info("清理了 {} 条过期的视图浏览缓存", removedCount);
        }
    }
}
