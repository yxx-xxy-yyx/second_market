package com.echoofmemories.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.echoofmemories.project.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * 商品数据访问层
 * 
 * @author EchoOfMemories
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    
    Page<Product> selectProductPageWithUser(Page<Product> page, 
                                           @Param("keyword") String keyword,
                                           @Param("category") String category,
                                           @Param("minPrice") BigDecimal minPrice,
                                           @Param("maxPrice") BigDecimal maxPrice,
                                           @Param("minCondition") Integer minCondition,
                                           @Param("maxCondition") Integer maxCondition,
                                           @Param("sortBy") String sortBy,
                                           @Param("status") Integer status,
                                           @Param("userId") Long userId,
                                           @Param("schoolId") Long schoolId);
    
    Product selectProductWithUserById(@Param("id") Long id);
}
