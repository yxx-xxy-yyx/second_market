package com.echoofmemories.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.echoofmemories.project.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单数据访问层
 * 
 * @author EchoOfMemories
 */
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
    
    Page<Orders> selectOrderPageWithDetails(Page<Orders> page,
                                           @Param("buyerId") Long buyerId,
                                           @Param("sellerId") Long sellerId,
                                           @Param("status") Integer status);
    
    Orders selectOrderWithDetailsById(@Param("id") Long id);
}

