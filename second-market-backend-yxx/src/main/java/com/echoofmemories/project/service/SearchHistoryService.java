package com.echoofmemories.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.echoofmemories.project.entity.SearchHistory;

import java.util.List;

public interface SearchHistoryService extends IService<SearchHistory> {

    void recordSearch(Long userId, String keyword);

    List<SearchHistory> getVisibleHistory(Long userId, int limit);

    void clearHistory(Long userId);
}
