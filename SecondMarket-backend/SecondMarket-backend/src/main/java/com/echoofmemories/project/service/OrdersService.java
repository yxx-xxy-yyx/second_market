package com.echoofmemories.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.echoofmemories.project.entity.Orders;

/**
 * 订单服务接口
 * 
 * @author EchoOfMemories
 */
public interface OrdersService extends IService<Orders> {
    
    Orders createOrder(Long buyerId, Long productId);
    
    boolean payOrder(Long orderId, Long userId);
    
    boolean finishOrder(Long orderId, Long userId);
    
    boolean cancelOrder(Long orderId, Long userId);
    
    Orders getOrderWithDetailsById(Long id);
    
    Page<Orders> getBuyerOrderPage(Integer pageNum, Integer pageSize, Long buyerId, Integer status);
    
    Page<Orders> getSellerOrderPage(Integer pageNum, Integer pageSize, Long sellerId, Integer status);
}

