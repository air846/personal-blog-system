package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 文章Mapper接口
 * 
 * @author blog
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 分页查询文章列表（包含分类和作者信息）
     */
    IPage<Article> selectArticlePage(Page<Article> page, @Param("categoryId") Long categoryId, 
                                   @Param("status") String status, @Param("keyword") String keyword);

    /**
     * 增加文章阅读量
     */
    int incrementViewCount(@Param("id") Long id);

    /**
     * 增加文章点赞数
     */
    int incrementLikeCount(@Param("id") Long id);

    /**
     * 减少文章点赞数
     */
    int decrementLikeCount(@Param("id") Long id);

    /**
     * 增加文章评论数
     */
    int incrementCommentCount(@Param("id") Long id);

    /**
     * 减少文章评论数
     */
    int decrementCommentCount(@Param("id") Long id);
}