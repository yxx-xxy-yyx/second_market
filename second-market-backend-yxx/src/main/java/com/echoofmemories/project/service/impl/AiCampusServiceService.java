package com.echoofmemories.project.service.impl;

import com.echoofmemories.project.dto.AiCampusServiceRequest;
import com.echoofmemories.project.dto.AiCampusServiceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 校园专属服务
 * 提供跑腿、快递、辅导等校园服务推荐
 *
 * @author echo
 * @since 2025-01-27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiCampusServiceService {

    private final LlmClientService llmClient;

    public AiCampusServiceResponse campusService(AiCampusServiceRequest request, Long userId) {
        log.info("校园专属服务 - 用户ID: {}, 类型: {}", userId, request.getServiceType());

        AiCampusServiceResponse response = new AiCampusServiceResponse();
        response.setServiceType(request.getServiceType());

        // 基于 serviceType 派生的伪随机种子，保证相同输入结果可复现
        int seed = request.getServiceType() == null ? 0 : request.getServiceType().hashCode();

        List<Map<String, Object>> matches = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Map<String, Object> match = new HashMap<>();
            match.put("id", i + 1);
            match.put("title", request.getServiceType() + "服务 " + (i + 1));
            // 伪随机价格：10-60 范围，基于 seed 派生
            double price = 10 + (Math.abs(seed * (i + 1)) % 51);
            match.put("price", price);
            // 伪随机评分：4.0-5.0 范围
            double rating = 4.0 + ((Math.abs(seed + i * 7) % 10) / 10.0);
            match.put("rating", rating);
            matches.add(match);
        }
        response.setMatches(matches);

        response.setSuggestion("建议选择评分最高的服务");
        response.setTips(Arrays.asList(
                "提前确认服务时间",
                "选择有评价的服务商",
                "注意保管好个人物品"
        ));
        response.setPriceEstimate(25.0);
        response.setPriceCurrency("CNY");

        return response;
    }
}
