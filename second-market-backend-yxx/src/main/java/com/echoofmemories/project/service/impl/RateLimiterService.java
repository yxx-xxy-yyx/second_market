package com.echoofmemories.project.service.impl;

import com.echoofmemories.project.config.AiConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 限流服务
 * 基于ConcurrentHashMap + AtomicInteger的内存限流器
 *
 * @author echo
 * @since 2025-01-27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private final AiConfig aiConfig;

    private final ConcurrentHashMap<Long, UserRateLimit> userRateLimits = new ConcurrentHashMap<>();

    /**
     * 检查用户请求限流（使用默认限制）
     */
    public boolean checkRateLimit(Long userId) {
        int limit = aiConfig.getFeatures().getRateLimit() != null
                ? aiConfig.getFeatures().getRateLimit()
                : 10;
        return checkRateLimit(userId, limit);
    }

    /**
     * 检查用户请求限流（自定义限制）
     */
    public boolean checkRateLimit(Long userId, int limit) {
        // 测试用户放行
        if (userId == null || userId == 0L) {
            return true;
        }

        UserRateLimit userLimit = userRateLimits.computeIfAbsent(userId, k -> new UserRateLimit());
        return userLimit.canRequest(limit);
    }

    /**
     * 用户限流信息内部类
     */
    private static class UserRateLimit {
        private final AtomicInteger requestCount = new AtomicInteger(0);
        private final AtomicLong lastResetTime = new AtomicLong(System.currentTimeMillis());

        public boolean canRequest(int limit) {
            long now = System.currentTimeMillis();
            long lastReset = lastResetTime.get();

            // 每分钟重置一次
            if (now - lastReset > 60000) {
                if (lastResetTime.compareAndSet(lastReset, now)) {
                    requestCount.set(0);
                }
            }

            return requestCount.incrementAndGet() <= limit;
        }
    }
}
