package com.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.entity.Article;

import java.util.List;

/**
 * 文章服务接口
 * 
 * @author blog
 */
public interface ArticleService extends IService<Article> {

    /**
     * 分页查询文章
     */
    IPage<Article> getArticlePage(Integer page, Integer size, Long categoryId, String status, String keyword);

    /**
     * 获取文章详情
     */
    Article getArticleDetail(Long id);

    /**
     * 创建文章
     */
    void createArticle(Article article, List<Long> tagIds);

    /**
     * 更新文章
     */
    void updateArticle(Article article, List<Long> tagIds);

    /**
     * 删除文章
     */
    void deleteArticle(Long id);

    /**
     * 发布文章
     */
    void publishArticle(Long id);

    /**
     * 点赞文章
     */
    void likeArticle(Long articleId, Long userId);

    /**
     * 取消点赞
     */
    void unlikeArticle(Long articleId, Long userId);

    /**
     * 获取热门文章
     */
    List<Article> getHotArticles(Integer limit);

    /**
     * 获取推荐文章
     */
    List<Article> getRecommendedArticles(Integer limit);

    /**
     * 搜索文章
     */
    IPage<Article> searchArticles(String keyword, Integer page, Integer size);

    /**
     * 增加阅读量
     */
    void incrementViewCount(Long id);
}