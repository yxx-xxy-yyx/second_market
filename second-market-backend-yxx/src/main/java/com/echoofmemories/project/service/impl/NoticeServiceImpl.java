package com.echoofmemories.project.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.echoofmemories.project.dto.NoticeRequest;
import com.echoofmemories.project.entity.Notice;
import com.echoofmemories.project.exception.CustomException;
import com.echoofmemories.project.mapper.NoticeMapper;
import com.echoofmemories.project.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 公告服务实现类
 * 
 * @author EchoOfMemories
 */
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    private final NoticeMapper noticeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Notice addNotice(NoticeRequest request, Long publisherId) {
        if (StrUtil.isBlank(request.getTitle()) || StrUtil.isBlank(request.getContent())) {
            throw new CustomException("标题和内容不能为空");
        }

        Notice notice = new Notice();
        notice.setType(request.getType() != null ? request.getType() : 0);
        notice.setTitle(request.getTitle());
        notice.setContent(request.getContent());
        notice.setStatus(request.getStatus() != null ? request.getStatus() : 0);
        notice.setPublisherId(publisherId);
        notice.setDeleted(0);

        this.save(notice);
        return notice;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateNotice(NoticeRequest request) {
        if (request.getId() == null) {
            throw new CustomException("公告ID不能为空");
        }

        Notice existingNotice = this.getById(request.getId());
        if (existingNotice == null || existingNotice.getDeleted() == 1) {
            throw new CustomException("公告不存在");
        }

        if (request.getType() != null) {
            existingNotice.setType(request.getType());
        }
        if (StrUtil.isNotBlank(request.getTitle())) {
            existingNotice.setTitle(request.getTitle());
        }
        if (StrUtil.isNotBlank(request.getContent())) {
            existingNotice.setContent(request.getContent());
        }
        if (request.getStatus() != null) {
            existingNotice.setStatus(request.getStatus());
        }

        return this.updateById(existingNotice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteNotice(Long id) {
        Notice notice = this.getById(id);
        if (notice == null || notice.getDeleted() == 1) {
            throw new CustomException("公告不存在");
        }
        return this.removeById(id);
    }

    @Override
    public Notice getNoticeById(Long id) {
        return noticeMapper.selectNoticeWithPublisherById(id);
    }

    @Override
    public Page<Notice> getNoticePage(Integer pageNum, Integer pageSize, Integer status, Integer type) {
        Page<Notice> page = new Page<>(pageNum, pageSize);
        return noticeMapper.selectNoticePageWithPublisher(page, status, type);
    }
}

