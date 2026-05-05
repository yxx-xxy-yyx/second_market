package com.echoofmemories.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.echoofmemories.project.entity.Product;

import java.math.BigDecimal;

/**
 * 商品服务接口
 * 
 * @author EchoOfMemories
 */
public interface ProductService extends IService<Product> {
    
    Page<Product> getProductPage(Integer pageNum, Integer pageSize, String keyword, 
                                String category, BigDecimal minPrice, BigDecimal maxPrice, 
                                Integer minCondition, Integer maxCondition, String sortBy,
                                Integer status, Long userId, Long schoolId);
    
    Product getProductWithUserById(Long id);
    
    boolean addProduct(Product product);
    
    boolean updateProduct(Product product);
    
    boolean deleteProduct(Long id);
    
    Product viewProduct(Long id, String userIdentifier);
}
