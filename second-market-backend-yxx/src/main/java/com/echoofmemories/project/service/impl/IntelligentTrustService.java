package com.echoofmemories.project.service.impl;

import com.echoofmemories.project.dto.AiIntelligentTrustRequest;
import com.echoofmemories.project.dto.AiIntelligentTrustResponse;
import com.echoofmemories.project.entity.Message;
import com.echoofmemories.project.entity.Orders;
import com.echoofmemories.project.entity.Product;
import com.echoofmemories.project.entity.User;
import com.echoofmemories.project.mapper.MessageMapper;
import com.echoofmemories.project.mapper.OrdersMapper;
import com.echoofmemories.project.mapper.ProductMapper;
import com.echoofmemories.project.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 智能托管服务
 *
 * @author echo
 * @since 2025-01-27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IntelligentTrustService {

    private final ProductMapper productMapper;
    private final UserMapper userMapper;
    private final OrdersMapper ordersMapper;
    private final MessageMapper messageMapper;

    public AiIntelligentTrustResponse intelligentTrust(AiIntelligentTrustRequest request, Long userId) {
        log.info("智能托管服务 - 用户ID: {}, 商品ID: {}", userId, request.getProductId());

        AiIntelligentTrustResponse response = new AiIntelligentTrustResponse();

        Product product = null;
        if (request.getProductId() != null) {
            product = productMapper.selectById(request.getProductId());
        }

        User seller = null;
        if (product != null && product.getUserId() != null) {
            seller = userMapper.selectById(product.getUserId());
        } else if (userId != null) {
            seller = userMapper.selectById(userId);
        }

        int autoReplyCount = 0;
        try {
            if (userId != null) {
                QueryWrapper<Message> mw = new QueryWrapper<>();
                mw.eq("receiver_id", userId);
                mw.ge("create_time", LocalDateTime.now().minusDays(7));
                Long c = messageMapper.selectCount(mw);
                autoReplyCount = c == null ? 0 : c.intValue();
            }
        } catch (Exception e) {
            log.warn("消息回复数统计失败: {}", e.getMessage());
            autoReplyCount = 0;
        }

        int negotiationCount = 0;
        try {
            if (userId != null) {
                QueryWrapper<Orders> ow = new QueryWrapper<>();
                ow.eq("buyer_id", userId).eq("deleted", 0);
                Long c = ordersMapper.selectCount(ow);
                negotiationCount = c == null ? 0 : c.intValue();
            }
        } catch (Exception e) {
            log.warn("协商次数统计失败: {}", e.getMessage());
        }

        int priceAdjustmentCount = 0;
        try {
            if (product != null) {
                int vc = product.getViewCount() == null ? 0 : product.getViewCount();
                priceAdjustmentCount = vc > 50 ? Math.min(10, vc / 20) : 0;
            }
        } catch (Exception e) {
            priceAdjustmentCount = 0;
        }

        String status = "limited";
        boolean enabled = false;
        if (seller != null && seller.getCreditScore() != null && seller.getCreditScore() >= 90) {
            status = "active";
            enabled = true;
        } else if (seller != null && seller.getCreditScore() != null && seller.getCreditScore() >= 60) {
            status = "normal";
            enabled = true;
        }

        String lastActivity = LocalDateTime.now().toString();
        try {
            if (userId != null) {
                QueryWrapper<Orders> ow = new QueryWrapper<>();
                ow.in("buyer_id", userId).or(w -> w.in("seller_id", userId));
                ow.eq("deleted", 0);
                ow.orderByDesc("create_time");
                ow.last("LIMIT 1");
                Orders last = ordersMapper.selectOne(ow);
                if (last != null && last.getCreateTime() != null) {
                    lastActivity = last.getCreateTime().toString();
                }
            }
        } catch (Exception e) {
            log.warn("最近活动时间查询失败: {}", e.getMessage());
        }

        response.setEnabled(enabled);
        response.setAutoReplyCount(autoReplyCount);
        response.setNegotiationCount(negotiationCount);
        response.setPriceAdjustmentCount(priceAdjustmentCount);
        response.setStatus(status);
        response.setLastActivity(lastActivity);

        log.info("智能托管结果 - productId={}, enabled={}, status={}, autoReply={}, negotiation={}, priceAdj={}",
                request.getProductId(), enabled, status, autoReplyCount, negotiationCount, priceAdjustmentCount);

        return response;
    }
}
