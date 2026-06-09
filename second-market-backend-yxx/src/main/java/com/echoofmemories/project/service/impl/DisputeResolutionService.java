package com.echoofmemories.project.service.impl;

import com.echoofmemories.project.dto.AiDisputeResolutionRequest;
import com.echoofmemories.project.dto.AiDisputeResolutionResponse;
import com.echoofmemories.project.entity.Orders;
import com.echoofmemories.project.mapper.OrdersMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * AI纠纷仲裁服务
 *
 * @author echo
 * @since 2025-01-27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DisputeResolutionService {

    private final OrdersMapper ordersMapper;

    public AiDisputeResolutionResponse resolveDispute(AiDisputeResolutionRequest request, Long userId) {
        log.info("纠纷仲裁 - 用户ID: {}, 订单ID: {}", userId, request.getOrderId());

        AiDisputeResolutionResponse response = new AiDisputeResolutionResponse();

        Orders order = null;
        if (request.getOrderId() != null) {
            order = ordersMapper.selectById(request.getOrderId());
        }

        List<String> keyFindings = new ArrayList<>();
        double refundSuggestion = 0.0;
        String riskLevel = "low";
        String suggestedResolution;
        String nextSteps;
        int confidenceScore = 80;

        if (order == null) {
            keyFindings.add("未查询到订单信息，请确认订单ID");
            suggestedResolution = "无法自动仲裁，建议联系客服介入";
            nextSteps = "1. 确认订单ID是否正确  2. 提供纠纷描述与证据  3. 等待客服人工处理";
            confidenceScore = 60;
        } else {
            if (order.getAmount() != null) {
                keyFindings.add("订单金额：¥" + order.getAmount());
            }
            if (order.getStatus() != null) {
                keyFindings.add("订单状态：" + orderStatusText(order.getStatus()));
            }
            if (order.getCreateTime() != null) {
                keyFindings.add("下单时间：" + order.getCreateTime());
            }
            if (request.getDisputeDescription() != null && !request.getDisputeDescription().isEmpty()) {
                keyFindings.add("纠纷描述：" + request.getDisputeDescription());
            }
            if (request.getEvidenceUrls() != null && !request.getEvidenceUrls().isEmpty()) {
                keyFindings.add("已提供 " + request.getEvidenceUrls().size() + " 条证据");
            }

            if (order.getStatus() != null && order.getStatus() >= 3) {
                keyFindings.add("订单已进入完成/后序流程，可启动退款判断");
            }

            double amount = order.getAmount() == null ? 0.0 : order.getAmount().doubleValue();
            if (amount > 1000) {
                riskLevel = "high";
                refundSuggestion = Math.min(100.0, 70.0);
                confidenceScore = 75;
            } else if (amount > 200) {
                riskLevel = "medium";
                refundSuggestion = 50.0;
                confidenceScore = 85;
            } else {
                riskLevel = "low";
                refundSuggestion = 30.0;
                confidenceScore = 90;
            }

            String desc = request.getDisputeDescription();
            if (desc != null) {
                String lower = desc.toLowerCase();
                if (lower.contains("质量") || lower.contains("假") || lower.contains("损坏")) {
                    refundSuggestion = Math.min(100.0, refundSuggestion + 20.0);
                    keyFindings.add("涉及商品质量/真伪问题，建议卖家提供证明");
                }
                if (lower.contains("未发货") || lower.contains("没收到") || lower.contains("未收到")) {
                    refundSuggestion = 100.0;
                    keyFindings.add("涉及物流未送达，建议优先全额退款");
                }
                if (lower.contains("描述不符") || lower.contains("不符")) {
                    refundSuggestion = Math.max(refundSuggestion, 60.0);
                    keyFindings.add("存在描述不符争议，建议双方提供图文证据");
                }
            }

            suggestedResolution = "根据订单金额与纠纷描述，建议卖家承担 "
                    + String.format("%.0f", refundSuggestion) + "% 退款责任，双方协商解决";
            nextSteps = "1. 双方确认解决方案  2. 卖家执行退款操作  3. 买家确认收到退款  4. 完成纠纷处理";
        }

        response.setCaseId("DISPUTE-" + System.currentTimeMillis());
        response.setSuggestedResolution(suggestedResolution);
        response.setRefundSuggestion(refundSuggestion);
        response.setKeyFindings(keyFindings);
        response.setRiskLevel(riskLevel);
        response.setNextSteps(nextSteps);
        response.setConfidenceScore(confidenceScore);

        log.info("纠纷仲裁结果 - orderId={}, refund={}%, risk={}, confidence={}",
                request.getOrderId(), refundSuggestion, riskLevel, confidenceScore);

        return response;
    }

    private String orderStatusText(int status) {
        switch (status) {
            case 0:
                return "待支付";
            case 1:
                return "已支付/待发货";
            case 2:
                return "已发货/待收货";
            case 3:
                return "交易完成";
            case 4:
                return "已成交";
            case -1:
                return "已取消";
            default:
                return "状态-" + status;
        }
    }
}
