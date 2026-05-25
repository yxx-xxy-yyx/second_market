package com.echoofmemories.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.echoofmemories.project.entity.Notice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 公告数据访问层
 * 
 * @author EchoOfMemories
 */
@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {
    
    Page<Notice> selectNoticePageWithPublisher(Page<Notice> page, 
                                              @Param("status") Integer status,
                                              @Param("type") Integer type);
    
    Notice selectNoticeWithPublisherById(@Param("id") Long id);
}

