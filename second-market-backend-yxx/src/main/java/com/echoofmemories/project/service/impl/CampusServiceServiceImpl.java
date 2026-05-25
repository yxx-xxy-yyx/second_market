package com.echoofmemories.project.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.echoofmemories.project.entity.CampusService;
import com.echoofmemories.project.mapper.CampusServiceMapper;
import com.echoofmemories.project.service.CampusServiceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 校园周边服务 Service 实现类
 */
@Service
public class CampusServiceServiceImpl extends ServiceImpl<CampusServiceMapper, CampusService> implements CampusServiceService {

    @Resource
    private CampusServiceMapper campusServiceMapper;

    @Override
    public Page<CampusService> getPage(Page<CampusService> page, Integer type, Long schoolId) {
        return campusServiceMapper.selectCampusServicePage(page, type, schoolId);
    }

    @Override
    public CampusService getDetail(Long id) {
        return campusServiceMapper.selectCampusServiceDetail(id);
    }
}
