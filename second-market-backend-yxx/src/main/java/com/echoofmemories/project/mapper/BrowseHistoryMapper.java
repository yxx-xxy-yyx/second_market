package com.echoofmemories.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.echoofmemories.project.entity.BrowseHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 浏览记录Mapper接口
 */
@Mapper
public interface BrowseHistoryMapper extends BaseMapper<BrowseHistory> {

    /**
     * 分页查询用户浏览记录
     */
    IPage<BrowseHistory> selectHistoryPage(Page<BrowseHistory> page, 
                                          @Param("userId") Long userId,
                                          @Param("schoolId") Long schoolId);
}
