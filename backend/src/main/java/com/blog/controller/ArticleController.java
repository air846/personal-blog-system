package com.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.common.Result;
import com.blog.dto.ArticleQueryRequest;
import com.blog.dto.ArticleRequest;
import com.blog.entity.Article;
import com.blog.service.ArticleService;
import com.blog.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;

/**
 * 文章Controller
 * 
 * @author blog
 */
@Slf4j
@RestController
@RequestMapping("/article")
@RequiredArgsConstructor
@Tag(name = "文章管理", description = "文章CRUD、查询、点赞等相关接口")
public class ArticleController {

    private final ArticleService articleService;
    private final JwtUtil jwtUtil;

    @GetMapping("/list")
    @Operation(summary = "分页查询文章列表", description = "支持按分类、状态、关键词筛选")
    public Result<IPage<Article>> getArticleList(ArticleQueryRequest request) {
        try {
            IPage<Article> page = articleService.getArticlePage(
                request.getPage(), request.getSize(), 
                request.getCategoryId(), request.getStatus(), request.getKeyword()
            );
            return Result.success(page);
        } catch (Exception e) {
            log.error("查询文章列表失败", e);
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取文章详情", description = "根据文章ID获取文章详情，会自动增加阅读量")
    public Result<Article> getArticleDetail(@PathVariable Long id) {
        try {
            Article article = articleService.getArticleDetail(id);
            return Result.success(article);
        } catch (Exception e) {
            log.error("获取文章详情失败", e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "创建文章", description = "创建新文章，需要登录")
    public Result<Void> createArticle(@Valid @RequestBody ArticleRequest request, 
                                    HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromRequest(httpRequest);
            if (userId == null) {
                return Result.unauthorized("未登录");
            }

            Article article = new Article();
            BeanUtils.copyProperties(request, article);
            article.setAuthorId(userId);

            articleService.createArticle(article, request.getTagIds());
            return Result.success();
        } catch (Exception e) {
            log.error("创建文章失败", e);
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新文章", description = "更新文章信息，需要登录且是文章作者")
    public Result<Void> updateArticle(@PathVariable Long id, 
                                    @Valid @RequestBody ArticleRequest request,
                                    HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromRequest(httpRequest);
            if (userId == null) {
                return Result.unauthorized("未登录");
            }

            // 检查文章是否存在且是否为作者
            Article existingArticle = articleService.getById(id);
            if (existingArticle == null) {
                return Result.notFound("文章不存在");
            }
            if (!existingArticle.getAuthorId().equals(userId)) {
                return Result.forbidden("无权限修改此文章");
            }

            Article article = new Article();
            BeanUtils.copyProperties(request, article);
            article.setId(id);

            articleService.updateArticle(article, request.getTagIds());
            return Result.success();
        } catch (Exception e) {
            log.error("更新文章失败", e);
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除文章", description = "删除文章，需要登录且是文章作者")
    public Result<Void> deleteArticle(@PathVariable Long id, 
                                    HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromRequest(httpRequest);
            if (userId == null) {
                return Result.unauthorized("未登录");
            }

            // 检查文章是否存在且是否为作者
            Article existingArticle = articleService.getById(id);
            if (existingArticle == null) {
                return Result.notFound("文章不存在");
            }
            if (!existingArticle.getAuthorId().equals(userId)) {
                return Result.forbidden("无权限删除此文章");
            }

            articleService.deleteArticle(id);
            return Result.success();
        } catch (Exception e) {
            log.error("删除文章失败", e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/publish")
    @Operation(summary = "发布文章", description = "将草稿文章发布，需要登录且是文章作者")
    public Result<Void> publishArticle(@PathVariable Long id, 
                                     HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromRequest(httpRequest);
            if (userId == null) {
                return Result.unauthorized("未登录");
            }

            // 检查文章是否存在且是否为作者
            Article existingArticle = articleService.getById(id);
            if (existingArticle == null) {
                return Result.notFound("文章不存在");
            }
            if (!existingArticle.getAuthorId().equals(userId)) {
                return Result.forbidden("无权限发布此文章");
            }

            articleService.publishArticle(id);
            return Result.success();
        } catch (Exception e) {
            log.error("发布文章失败", e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/like")
    @Operation(summary = "点赞文章", description = "给文章点赞，需要登录")
    public Result<Void> likeArticle(@PathVariable Long id, 
                                  HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromRequest(httpRequest);
            if (userId == null) {
                return Result.unauthorized("未登录");
            }

            articleService.likeArticle(id, userId);
            return Result.success();
        } catch (Exception e) {
            log.error("点赞文章失败", e);
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/like")
    @Operation(summary = "取消点赞", description = "取消文章点赞，需要登录")
    public Result<Void> unlikeArticle(@PathVariable Long id, 
                                    HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromRequest(httpRequest);
            if (userId == null) {
                return Result.unauthorized("未登录");
            }

            articleService.unlikeArticle(id, userId);
            return Result.success();
        } catch (Exception e) {
            log.error("取消点赞失败", e);
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/hot")
    @Operation(summary = "获取热门文章", description = "根据阅读量获取热门文章")
    public Result<List<Article>> getHotArticles(@RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<Article> articles = articleService.getHotArticles(limit);
            return Result.success(articles);
        } catch (Exception e) {
            log.error("获取热门文章失败", e);
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/recommend")
    @Operation(summary = "获取推荐文章", description = "获取推荐的文章")
    public Result<List<Article>> getRecommendArticles(@RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<Article> articles = articleService.getRecommendArticles(limit);
            return Result.success(articles);
        } catch (Exception e) {
            log.error("获取推荐文章失败", e);
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/search")
    @Operation(summary = "搜索文章", description = "根据关键词搜索文章")
    public Result<IPage<Article>> searchArticles(@RequestParam String keyword,
                                               @RequestParam(defaultValue = "1") Integer page,
                                               @RequestParam(defaultValue = "10") Integer size) {
        try {
            IPage<Article> result = articleService.searchArticles(keyword, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("搜索文章失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 从请求中获取用户ID
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            return jwtUtil.getUserIdFromToken(token);
        }
        return null;
    }
}