package com.blog.controller;

import com.blog.common.Result;
import com.blog.entity.Category;
import com.blog.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类Controller
 * 
 * @author blog
 */
@Slf4j
@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
@Tag(name = "分类管理", description = "分类CRUD相关接口")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/list")
    @Operation(summary = "获取分类列表", description = "获取所有启用的分类")
    public Result<List<Category>> getCategoryList() {
        try {
            List<Category> categories = categoryService.list();
            return Result.success(categories);
        } catch (Exception e) {
            log.error("获取分类列表失败", e);
            return Result.error(e.getMessage());
        }
    }
}