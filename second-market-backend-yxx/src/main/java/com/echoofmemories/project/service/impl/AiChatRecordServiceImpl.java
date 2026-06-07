package com.echoofmemories.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.echoofmemories.project.entity.AiChatRecord;
import com.echoofmemories.project.mapper.AiChatRecordMapper;
import com.echoofmemories.project.service.AiChatRecordService;
import org.springframework.stereotype.Service;

@Service
public class AiChatRecordServiceImpl extends ServiceImpl<AiChatRecordMapper, AiChatRecord> implements AiChatRecordService {
}
