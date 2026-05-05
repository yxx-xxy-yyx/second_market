package com.echoofmemories.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.echoofmemories.project.dto.NoticeRequest;
import com.echoofmemories.project.entity.Notice;

/**
 * 公告服务接口
 * 
 * @author EchoOfMemories
 */
public interface NoticeService extends IService<Notice> {
    
    Notice addNotice(NoticeRequest request, Long publisherId);
    
    boolean updateNotice(NoticeRequest request);
    
    boolean deleteNotice(Long id);
    
    Notice getNoticeById(Long id);
    
    Page<Notice> getNoticePage(Integer pageNum, Integer pageSize, Integer status, Integer type);
}

