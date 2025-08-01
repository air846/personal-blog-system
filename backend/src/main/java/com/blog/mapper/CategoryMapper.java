package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分类Mapper接口
 * 
 * @author blog
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}