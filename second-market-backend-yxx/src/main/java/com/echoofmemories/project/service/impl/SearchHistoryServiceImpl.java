package com.echoofmemories.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.echoofmemories.project.entity.SearchHistory;
import com.echoofmemories.project.mapper.SearchHistoryMapper;
import com.echoofmemories.project.service.SearchHistoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class SearchHistoryServiceImpl extends ServiceImpl<SearchHistoryMapper, SearchHistory> implements SearchHistoryService {

    private static final int MAX_QUERY_SIZE = 100;

    @Override
    @Transactional
    public void recordSearch(Long userId, String keyword) {
        String normalizedKeyword = normalizeKeyword(keyword);
        if (normalizedKeyword == null) {
            return;
        }

        SearchHistory history = new SearchHistory();
        history.setUserId(userId);
        history.setKeyword(normalizedKeyword);
        history.setDeleted(0);
        history.setCreateTime(LocalDateTime.now());
        history.setUpdateTime(LocalDateTime.now());
        save(history);
    }

    @Override
    public List<SearchHistory> getVisibleHistory(Long userId, int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 50));
        LambdaQueryWrapper<SearchHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SearchHistory::getUserId, userId)
                .eq(SearchHistory::getDeleted, 0)
                .orderByDesc(SearchHistory::getCreateTime)
                .last("LIMIT " + MAX_QUERY_SIZE);

        List<SearchHistory> records = list(wrapper);
        List<SearchHistory> result = new ArrayList<>();
        Set<String> seenKeywords = new LinkedHashSet<>();
        for (SearchHistory record : records) {
            String keyword = record.getKeyword();
            if (keyword == null || seenKeywords.contains(keyword)) {
                continue;
            }
            seenKeywords.add(keyword);
            result.add(record);
            if (result.size() >= safeLimit) {
                break;
            }
        }
        return result;
    }

    @Override
    @Transactional
    public void clearHistory(Long userId) {
        SearchHistory update = new SearchHistory();
        update.setDeleted(1);
        update.setUpdateTime(LocalDateTime.now());

        LambdaQueryWrapper<SearchHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SearchHistory::getUserId, userId)
                .eq(SearchHistory::getDeleted, 0);
        update(update, wrapper);
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null) {
            return null;
        }
        String normalized = keyword.trim();
        if (normalized.isEmpty()) {
            return null;
        }
        return normalized.length() > 100 ? normalized.substring(0, 100) : normalized;
    }
}
