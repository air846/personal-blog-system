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

    @GetMapping("/{id}")
    @Operation(summary = "获取分类详情", description = "根据分类ID获取分类详情")
    public Result<Category> getCategoryDetail(@PathVariable Long id) {
        try {
            Category category = categoryService.getById(id);
            if (category == null) {
                return Result.notFound("分类不存在");
            }
            return Result.success(category);
        } catch (Exception e) {
            log.error("获取分类详情失败", e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "创建分类", description = "创建新分类，需要管理员权限")
    public Result<Void> createCategory(@RequestBody Category category) {
        try {
            categoryService.save(category);
            return Result.success();
        } catch (Exception e) {
            log.error("创建分类失败", e);
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新分类", description = "更新分类信息，需要管理员权限")
    public Result<Void> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        try {
            category.setId(id);
            categoryService.updateById(category);
            return Result.success();
        } catch (Exception e) {
            log.error("更新分类失败", e);
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除分类", description = "删除分类，需要管理员权限")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.removeById(id);
            return Result.success();
        } catch (Exception e) {
            log.error("删除分类失败", e);
            return Result.error(e.getMessage());
        }
    }
}