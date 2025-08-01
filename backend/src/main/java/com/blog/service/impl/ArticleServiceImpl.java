package com.blog.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.Article;
import com.blog.mapper.ArticleMapper;
import com.blog.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章Service实现类
 * 
 * @author blog
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Override
    public IPage<Article> getArticlePage(Integer page, Integer size, Long categoryId, String status, String keyword) {
        Page<Article> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        
        // 分类过滤
        if (categoryId != null) {
            wrapper.eq(Article::getCategoryId, categoryId);
        }
        
        // 状态过滤
        if (StrUtil.isNotBlank(status)) {
            wrapper.eq(Article::getStatus, status);
        }
        
        // 关键词搜索
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Article::getTitle, keyword)
                    .or()
                    .like(Article::getSummary, keyword));
        }
        
        // 排序：置顶 -> 发布时间 -> 创建时间
        wrapper.orderByDesc(Article::getIsTop)
               .orderByDesc(Article::getPublishTime)
               .orderByDesc(Article::getCreateTime);
        
        return page(pageParam, wrapper);
    }

    @Override
    public Article getArticleDetail(Long id) {
        Article article = getById(id);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }
        
        // 增加阅读量
        incrementViewCount(id);
        
        return article;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createArticle(Article article, List<Long> tagIds) {
        // 设置默认值
        if (article.getViewCount() == null) {
            article.setViewCount(0L);
        }
        if (article.getLikeCount() == null) {
            article.setLikeCount(0L);
        }
        if (article.getCommentCount() == null) {
            article.setCommentCount(0L);
        }
        if (article.getIsTop() == null) {
            article.setIsTop(0);
        }
        if (article.getIsRecommend() == null) {
            article.setIsRecommend(0);
        }
        
        // 保存文章
        save(article);
        
        // TODO: 保存文章标签关联关系
        // 这里需要实现文章标签关联的保存逻辑
        
        log.info("文章创建成功：{}", article.getTitle());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateArticle(Article article, List<Long> tagIds) {
        Article existingArticle = getById(article.getId());
        if (existingArticle == null) {
            throw new RuntimeException("文章不存在");
        }
        
        // 更新文章
        updateById(article);
        
        // TODO: 更新文章标签关联关系
        
        log.info("文章更新成功：{}", article.getTitle());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteArticle(Long id) {
        Article article = getById(id);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }
        
        // 删除文章
        removeById(id);
        
        // TODO: 删除文章标签关联关系
        
        log.info("文章删除成功：{}", article.getTitle());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishArticle(Long id) {
        Article article = getById(id);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }
        
        article.setStatus("PUBLISHED");
        article.setPublishTime(LocalDateTime.now());
        updateById(article);
        
        log.info("文章发布成功：{}", article.getTitle());
    }

    @Override
    public void incrementViewCount(Long id) {
        baseMapper.incrementViewCount(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void likeArticle(Long articleId, Long userId) {
        // TODO: 检查用户是否已经点赞
        // 这里需要实现点赞逻辑，包括检查是否已点赞、增加点赞数等
        
        // 增加文章点赞数
        baseMapper.incrementLikeCount(articleId);
        
        log.info("用户{}点赞文章{}", userId, articleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlikeArticle(Long articleId, Long userId) {
        // TODO: 检查用户是否已经点赞
        // 这里需要实现取消点赞逻辑
        
        // 减少文章点赞数
        baseMapper.decrementLikeCount(articleId);
        
        log.info("用户{}取消点赞文章{}", userId, articleId);
    }

    @Override
    @Cacheable(value = "hotArticles", key = "#limit")
    public List<Article> getHotArticles(Integer limit) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getStatus, "PUBLISHED")
               .orderByDesc(Article::getViewCount)
               .last("LIMIT " + limit);
        
        return list(wrapper);
    }

    @Override
    @Cacheable(value = "recommendArticles", key = "#limit")
    public List<Article> getRecommendArticles(Integer limit) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getStatus, "PUBLISHED")
               .eq(Article::getIsRecommend, 1)
               .orderByDesc(Article::getPublishTime)
               .last("LIMIT " + limit);
        
        return list(wrapper);
    }

    @Override
    public IPage<Article> searchArticles(String keyword, Integer page, Integer size) {
        Page<Article> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getStatus, "PUBLISHED")
               .and(w -> w.like(Article::getTitle, keyword)
                       .or()
                       .like(Article::getSummary, keyword)
                       .or()
                       .like(Article::getContent, keyword))
               .orderByDesc(Article::getPublishTime);
        
        return page(pageParam, wrapper);
    }
}