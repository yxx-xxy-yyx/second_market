package com.echoofmemories.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.echoofmemories.project.entity.BrowseHistory;

/**
 * 浏览记录服务接口
 */
public interface BrowseHistoryService extends IService<BrowseHistory> {

    /**
     * 记录浏览历史
     */
    void addHistory(Long userId, Long productId);

    /**
     * 获取用户浏览记录分页
     */
    IPage<BrowseHistory> getHistoryPage(Page<BrowseHistory> page, Long userId, Long schoolId);

    /**
     * 清空用户浏览记录
     */
    void clearHistory(Long userId);
}
