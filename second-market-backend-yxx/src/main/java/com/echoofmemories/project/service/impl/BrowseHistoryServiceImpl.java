package com.echoofmemories.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.echoofmemories.project.entity.BrowseHistory;
import com.echoofmemories.project.mapper.BrowseHistoryMapper;
import com.echoofmemories.project.service.BrowseHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 浏览记录服务实现类
 */
@Service
public class BrowseHistoryServiceImpl extends ServiceImpl<BrowseHistoryMapper, BrowseHistory> implements BrowseHistoryService {

    @Autowired
    private BrowseHistoryMapper browseHistoryMapper;

    @Override
    @Transactional
    public void addHistory(Long userId, Long productId) {
        LambdaQueryWrapper<BrowseHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BrowseHistory::getUserId, userId).eq(BrowseHistory::getProductId, productId);
        BrowseHistory existing = getOne(queryWrapper);

        if (existing != null) {
            existing.setUpdateTime(LocalDateTime.now());
            updateById(existing);
        } else {
            BrowseHistory history = new BrowseHistory();
            history.setUserId(userId);
            history.setProductId(productId);
            save(history);
        }
    }

    @Override
    public IPage<BrowseHistory> getHistoryPage(Page<BrowseHistory> page, Long userId, Long schoolId) {
        return browseHistoryMapper.selectHistoryPage(page, userId, schoolId);
    }

    @Override
    @Transactional
    public void clearHistory(Long userId) {
        LambdaQueryWrapper<BrowseHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BrowseHistory::getUserId, userId);
        remove(queryWrapper);
    }
}
