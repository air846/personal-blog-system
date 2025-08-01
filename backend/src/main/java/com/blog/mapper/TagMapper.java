package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 标签Mapper接口
 * 
 * @author blog
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {
}