package com.echoofmemories.project.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.echoofmemories.project.entity.Orders;
import com.echoofmemories.project.entity.Product;
import com.echoofmemories.project.exception.CustomException;
import com.echoofmemories.project.mapper.OrdersMapper;
import com.echoofmemories.project.service.OrdersService;
import com.echoofmemories.project.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * 订单服务实现类
 * 
 * @author EchoOfMemories
 */
@Service
@RequiredArgsConstructor
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    private final OrdersMapper ordersMapper;
    private final ProductService productService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Orders createOrder(Long buyerId, Long productId) {
        Product product = productService.getById(productId);
        if (product == null) {
            throw new CustomException("商品不存在");
        }

        if (product.getDeleted() == 1) {
            throw new CustomException("商品已删除");
        }

        if (product.getStatus() == 4) {
            throw new CustomException("商品已售出");
        }

        if (product.getStatus() != 2) {
            throw new CustomException("商品未上架");
        }

        if (product.getUserId().equals(buyerId)) {
            throw new CustomException("不能购买自己的商品");
        }

        String orderNo = generateOrderNo();

        Orders order = new Orders();
        order.setOrderNo(orderNo);
        order.setBuyerId(buyerId);
        order.setSellerId(product.getUserId());
        order.setProductId(productId);
        order.setAmount(product.getPrice());
        order.setStatus(0);

        this.save(order);
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean payOrder(Long orderId, Long userId) {
        Orders order = this.getById(orderId);
        if (order == null) {
            throw new CustomException("订单不存在");
        }

        if (!order.getBuyerId().equals(userId)) {
            throw new CustomException("只有买家可以支付订单");
        }

        if (order.getStatus() != 0) {
            throw new CustomException("订单状态不正确");
        }

        order.setStatus(1);
        order.setPayTime(LocalDateTime.now());
        boolean updateOrder = this.updateById(order);

        if (updateOrder) {
            Product product = productService.getById(order.getProductId());
            product.setStatus(4);
            productService.updateById(product);
        }

        return updateOrder;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean finishOrder(Long orderId, Long userId) {
        Orders order = this.getById(orderId);
        if (order == null) {
            throw new CustomException("订单不存在");
        }

        if (!order.getBuyerId().equals(userId)) {
            throw new CustomException("只有买家可以确认收货");
        }

        if (order.getStatus() != 1) {
            throw new CustomException("订单状态不正确");
        }

        order.setStatus(2);
        order.setFinishTime(LocalDateTime.now());
        return this.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelOrder(Long orderId, Long userId) {
        Orders order = this.getById(orderId);
        if (order == null) {
            throw new CustomException("订单不存在");
        }

        if (!order.getBuyerId().equals(userId)) {
            throw new CustomException("只有买家可以取消订单");
        }

        if (order.getStatus() != 0) {
            throw new CustomException("只能取消待支付订单");
        }

        order.setStatus(3);
        boolean updateOrder = this.updateById(order);

        if (updateOrder) {
            Product product = productService.getById(order.getProductId());
            product.setStatus(2);
            productService.updateById(product);
        }

        return updateOrder;
    }

    @Override
    public Orders getOrderWithDetailsById(Long id) {
        Orders order = ordersMapper.selectOrderWithDetailsById(id);
        if (order == null) {
            throw new CustomException("订单不存在");
        }
        return order;
    }

    @Override
    public Page<Orders> getBuyerOrderPage(Integer pageNum, Integer pageSize, Long buyerId, Integer status) {
        Page<Orders> page = new Page<>(pageNum, pageSize);
        return ordersMapper.selectOrderPageWithDetails(page, buyerId, null, status);
    }

    @Override
    public Page<Orders> getSellerOrderPage(Integer pageNum, Integer pageSize, Long sellerId, Integer status) {
        Page<Orders> page = new Page<>(pageNum, pageSize);
        return ordersMapper.selectOrderPageWithDetails(page, null, sellerId, status);
    }

    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%06d", new Random().nextInt(1000000));
        return timestamp + random;
    }
}

