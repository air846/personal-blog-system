package com.blog.service.impl;

import com.blog.entity.Article;
import com.blog.mapper.ArticleMapper;
import com.blog.service.ArticleService;
import com.blog.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public Result getArticleList(int page, int size, Long categoryId) {
        int offset = (page - 1) * size;
        List<Article> articles = articleMapper.findPublishedArticles(offset, size, categoryId);
        int total = articleMapper.countPublishedArticles(categoryId);
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", articles);
        data.put("total", total);
        data.put("page", page);
        data.put("size", size);
        
        return Result.success(data);
    }

    @Override
    public Result getArticleDetail(Long id) {
        Article article = articleMapper.findById(id);
        if (article == null || article.getStatus() != 1) {
            return Result.error("文章不存在");
        }
        
        // 增加阅读量
        articleMapper.incrementViewCount(id);
        article.setViewCount(article.getViewCount() + 1);
        
        return Result.success(article);
    }

    @Override
    public Result createArticle(Article article) {
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        article.setViewCount(0);
        article.setLikeCount(0);
        article.setCommentCount(0);
        article.setIsTop(0);
        article.setIsRecommend(0);
        
        if (article.getStatus() == 1) {
            article.setPublishTime(LocalDateTime.now());
        }
        
        articleMapper.insert(article);
        return Result.success("创建成功");
    }

    @Override
    public Result updateArticle(Long id, Article article) {
        Article existingArticle = articleMapper.findById(id);
        if (existingArticle == null) {
            return Result.error("文章不存在");
        }
        
        article.setId(id);
        article.setUpdateTime(LocalDateTime.now());
        
        // 如果从草稿变为发布状态，设置发布时间
        if (existingArticle.getStatus() == 0 && article.getStatus() == 1) {
            article.setPublishTime(LocalDateTime.now());
        }
        
        articleMapper.update(article);
        return Result.success("更新成功");
    }

    @Override
    public Result deleteArticle(Long id) {
        Article article = articleMapper.findById(id);
        if (article == null) {
            return Result.error("文章不存在");
        }
        
        articleMapper.deleteById(id);
        return Result.success("删除成功");
    }

    @Override
    public Result publishArticle(Long id) {
        Article article = articleMapper.findById(id);
        if (article == null) {
            return Result.error("文章不存在");
        }
        
        article.setStatus(1);
        article.setPublishTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        
        articleMapper.update(article);
        return Result.success("发布成功");
    }

    @Override
    public Result likeArticle(Long id) {
        Article article = articleMapper.findById(id);
        if (article == null) {
            return Result.error("文章不存在");
        }
        
        articleMapper.incrementLikeCount(id);
        return Result.success("点赞成功");
    }

    @Override
    public Result unlikeArticle(Long id) {
        Article article = articleMapper.findById(id);
        if (article == null) {
            return Result.error("文章不存在");
        }
        
        articleMapper.decrementLikeCount(id);
        return Result.success("取消点赞成功");
    }

    @Override
    public Result getHotArticles(int limit) {
        List<Article> articles = articleMapper.findHotArticles(limit);
        return Result.success(articles);
    }

    @Override
    public Result getRecommendArticles(int limit) {
        List<Article> articles = articleMapper.findRecommendArticles(limit);
        return Result.success(articles);
    }

    @Override
    public Result searchArticles(String keyword, int page, int size) {
        int offset = (page - 1) * size;
        List<Article> articles = articleMapper.searchArticles(keyword, offset, size);
        int total = articleMapper.countSearchArticles(keyword);
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", articles);
        data.put("total", total);
        data.put("page", page);
        data.put("size", size);
        
        return Result.success(data);
    }

    @Override
    public Result getUserArticles(Long userId, int page, int size, Integer status) {
        int offset = (page - 1) * size;
        List<Article> articles = articleMapper.findUserArticles(userId, offset, size, status);
        int total = articleMapper.countUserArticles(userId, status);
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", articles);
        data.put("total", total);
        data.put("page", page);
        data.put("size", size);
        
        return Result.success(data);
    }
}