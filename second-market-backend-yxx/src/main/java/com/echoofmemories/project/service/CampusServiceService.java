package com.echoofmemories.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.echoofmemories.project.entity.CampusService;

/**
 * 校园周边服务 Service
 */
public interface CampusServiceService extends IService<CampusService> {
    Page<CampusService> getPage(Page<CampusService> page, Integer type, Long schoolId);

    CampusService getDetail(Long id);
}
