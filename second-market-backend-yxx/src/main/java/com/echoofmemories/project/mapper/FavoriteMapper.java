package com.echoofmemories.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.echoofmemories.project.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 收藏数据访问层
 * 
 * @author EchoOfMemories
 */
@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {
    
    Page<Favorite> selectFavoritePageWithProduct(Page<Favorite> page, @Param("userId") Long userId);
}

