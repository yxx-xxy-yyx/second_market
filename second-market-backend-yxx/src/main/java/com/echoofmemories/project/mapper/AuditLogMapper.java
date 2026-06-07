package com.echoofmemories.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.echoofmemories.project.entity.AuditLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuditLogMapper extends BaseMapper<AuditLog> {
}
