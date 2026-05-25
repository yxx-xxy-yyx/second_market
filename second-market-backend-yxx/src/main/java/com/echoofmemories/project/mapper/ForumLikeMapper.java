package com.echoofmemories.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.echoofmemories.project.entity.ForumLike;
import org.apache.ibatis.annotations.Mapper;

/**
 * 论坛点赞Mapper接口
 */
@Mapper
public interface ForumLikeMapper extends BaseMapper<ForumLike> {
}
